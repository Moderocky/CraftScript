package mx.kenzie.craftscript.environment;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.command.ScriptCommand;
import mx.kenzie.craftscript.disk.ScriptStorage;
import mx.kenzie.craftscript.script.*;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.SleepyList;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import static net.kyori.adventure.text.Component.text;

public class ScriptRuntime {

    static final int TIME = 3;
    final CommandSender owner;
    final int limit;
    final ScriptManager manager;
    final ScriptController controller;
    final ScriptStorage storage;
    final Map<AbstractScript, ScriptRunData> map = new ConcurrentHashMap<>();
    final Map<Executable<?>, TaskRunData> background = new ConcurrentHashMap<>();
    final List<Resource> resources = new SleepyList<>();

    public ScriptRuntime(ScriptStorage storage, ScriptManager manager, ScriptController controller, CommandSender owner, int limit) {
        this.storage = storage;
        this.owner = owner;
        this.limit = limit;
        this.manager = manager;
        this.controller = controller;
    }

    private static void printError(AbstractScript script, ScriptError error, CommandSender sender) {
        if (sender.isOp()) error.printStackTrace();
        final ColorProfile profile = ScriptCommand.COLOR_PROFILE;
        sender.sendMessage(Component.textOfChildren(Component.newline(), text('<', profile.pop()),
            text("Warning", profile.highlight()), text('>', profile.pop()), text(" The script '", profile.dark()),
            text(script.name(), profile.highlight()), text("' encountered an error and stopped.", profile.dark()),
            Component.newline(), text(error.getMessage(), profile.light()), Component.newline()));
        if (!error.hasContext()) return;
        final Context context = error.getContext();
        if (context.data().line == null) return;
        sender.sendMessage(
            Component.textOfChildren(text("Line " + context.getLine(), profile.highlight()), text(':', profile.pop()),
                Component.space(), context.data().line.prettyPrint(profile),
                Component.newline()));
    }

    @SuppressWarnings({"removal"})
    private static void killThread(Thread thread) {
        if (thread instanceof ScriptThread ours) ours.terminate();
        else try {
            thread.stop();
        } catch (UnsupportedOperationException ex) {
            thread.interrupt();
        }
    }

    public ScriptStorage getStorage() {
        return storage;
    }

    public void runTask(AbstractScript source, Executable<?> task, Context context) {
        this.controller.pool.submit(new TaskRunnable(this, source, task, context));
    }

    public void run(AbstractScript script) {
        // reusing a thread is good :)
        if (Thread.currentThread() instanceof ScriptThread) this.manager.runScript(script, owner);
        else if (limit > 0 && map.size() >= limit)
            throw new ScriptError("You have " + map.size() + " scripts running already.");
        else this.controller.pool.submit(new ScriptRunnable(this, script, owner));
    }

    @SuppressWarnings("MarkedForRemoval")
    public boolean terminate(AbstractScript script) {
        final ScriptRunData data = this.map.remove(script);
        if (data == null) return false;
        killThread(data.thread);
        this.background.values().removeIf(details -> {
            if (details.source != script) return false;
            killThread(details.thread);
            return true;
        });
        this.manager.unregisterListeners(script);
        return true;
    }

    public void registerResource(Resource resource) {
        this.resources.add(resource);
    }

    public void unregisterResource(Resource resource) {
        this.resources.remove(resource);
    }

    public void close() {
        try {
            for (final AbstractScript script : new ArrayList<>(this.map.keySet()))
                this.terminate(script);
            this.map.clear();
        } finally {
            for (final Resource resource : resources) resource.closeSafely();
            this.resources.clear();
            this.manager.close();
        }
    }

    private void report(AbstractScript script) {
        final ColorProfile profile = ScriptCommand.COLOR_PROFILE;
        this.owner.sendMessage(Component.textOfChildren(Component.newline(), text('<', profile.pop()),
            text("Warning", profile.highlight()), text('>', profile.pop()), text(" The script '", profile.dark()),
            text(script.name(), profile.highlight()), text("' ran for more than ", profile.dark()),
            text(TIME, profile.highlight()), text(" seconds without checking in.", profile.dark()), Component.newline(),
            text("This suggests the script was:", profile.light()), Component.newline(), text(" A) ", profile.pop()),
            text("stuck in an infinite loop.", profile.light()), Component.newline(), text(" B) ", profile.pop()),
            text("trying to do an impossible task.", profile.light()), Component.newline(),
            text("The script has been terminated.", profile.light()), Component.newline()));
    }

    public void checkIn() {
        final long now = System.currentTimeMillis(), cutOff = now - (TIME * 1000);
        for (final Map.Entry<AbstractScript, ScriptRunData> entry : new ArrayList<>(map.entrySet())) {
            final AbstractScript script = entry.getKey();
            final ScriptRunData data = entry.getValue();
            final long previous = data.checkIn().get();
            if (previous < 1 || previous > cutOff) continue;
            this.terminate(script);
            this.report(script);
        }
        for (final Map.Entry<Executable<?>, TaskRunData> entry : new ArrayList<>(background.entrySet())) {
            final TaskRunData data = entry.getValue();
            final AbstractScript script = data.source;
            final long previous = data.checkIn().get();
            if (previous < 1 || previous > cutOff) continue;
            this.terminate(script);
            this.report(script);
        }
    }

    public void checkIn(AbstractScript script) {
        if (script == null) return;
        final ScriptRunData data = map.get(script);
        if (data == null) return;
        data.checkIn().set(System.currentTimeMillis());
        for (final TaskRunData value : background.values()) value.checkIn().set(System.currentTimeMillis());
    }

    public void pauseCheckIn(AbstractScript script) {
        if (script == null) return;
        final ScriptRunData data = map.get(script);
        if (data == null) return;
        data.checkIn().set(0);
    }

    record ScriptRunnable(ScriptRuntime runtime, AbstractScript script, CommandSender owner) implements Runnable {

        @Override
        public void run() {
            final ScriptThread thread = (ScriptThread) Thread.currentThread();
            try {
                this.runtime.map.put(script, new ScriptRunData(thread, new AtomicLong(System.currentTimeMillis())));
                final ScriptManager manager = this.runtime.manager;
                try {
                    manager.runScript(script, owner);
                    while (manager.hasListeners(script)) {
                        this.runtime.pauseCheckIn(script); // it's just sleeping for now
                        LockSupport.parkNanos(1_000_000_000);
                    } // eats a task slot until script is killed
                } catch (ScriptDeath death) { // the script died, but we can re-use the thread
                    thread.resurrect();
                } catch (ThreadDeath death) { // planned termination
                    throw death;
                } catch (ScriptError error) {
                    printError(script, error, owner);
                } catch (Throwable ex) {
                    printError(script, new ScriptError("Error cause unknown.", ex), owner);
                }
            } finally {
                this.runtime.manager.unregisterListeners(script);
                this.runtime.map.remove(script);
                Thread.interrupted();
            }
        }

    }

    record TaskRunnable(ScriptRuntime runtime, AbstractScript script, Executable<?> task,
                        Context context) implements Runnable {

        @Override
        public void run() {
            final ScriptThread thread = (ScriptThread) Thread.currentThread();
            try {
                Context.setLocalContext(context);
                this.runtime.background.put(task,
                    new TaskRunData(thread, new AtomicLong(System.currentTimeMillis()), script));
                try {
                    this.task.execute(context);
                } catch (ScriptDeath death) { // shouldn't happen, but we don't want to launch a dead thread
                    thread.resurrect();
                } catch (ThreadDeath ignore) { // we probably don't need to kill this thread
                } catch (ScriptError error) {
                    printError(script, error, context.source());
                } catch (Throwable ex) {
                    printError(script, new ScriptError("Error in background task.", ex), context.source());
                }
            } finally {
                this.runtime.background.remove(task);
                Context.removeLocalContext();
            }
        }

    }

    record ScriptRunData(ScriptThread thread, AtomicLong checkIn) {

    }

    record TaskRunData(ScriptThread thread, AtomicLong checkIn, AbstractScript source) {

    }

}
