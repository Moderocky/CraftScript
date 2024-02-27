package mx.kenzie.craftscript.environment;

import mx.kenzie.craftscript.CraftScriptPlugin;
import mx.kenzie.craftscript.emitter.Event;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.kind.Kinds;
import mx.kenzie.craftscript.script.*;
import mx.kenzie.craftscript.script.core.LibraryStatement;
import mx.kenzie.craftscript.utility.BackgroundTaskExecutor;
import mx.kenzie.craftscript.utility.TaskExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ScriptController implements mx.kenzie.craftscript.utility.ScriptController {

    private static final Kind<?>[] kinds;
    private static final LibraryScript MINECRAFT = new LibraryScript("minecraft.script",
        new LibraryStatement(new MinecraftLibrary()));

    static {
//        Kinds.kinds.add(new PlayerClaimKind());
//        Kinds.kinds.add(new ClaimKind());
//        Kinds.kinds.add(new BlockHandleKind());
//        Kinds.kinds.add(new InventoryHandleKind());
//        Kinds.kinds.add(new ItemHandleKind());
        kinds = Kinds.kinds.toArray(new Kind[0]);
    }

    protected final ExecutorService pool = Executors.newCachedThreadPool(new ScriptThreadFactory());
    private final ConcurrentLinkedQueue<ScriptTask> queue = new ConcurrentLinkedQueue<>();
    private ScriptRuntime runtime;
    private final TaskExecutor executor = (context, supplier) -> {
        //<editor-fold desc="Main Thread Queue" defaultstate="collapsed">
        try {
            if (Bukkit.isPrimaryThread()) return supplier.get();
            this.checkIn(context);
            final ScriptTask task = new ScriptTask(supplier);
            this.submit(task);
            task.lock().acquire();
            final Object result = task.result().get();
            if (result instanceof Throwable throwable) throw throwable;
            this.checkIn(context);
            return result;
        } catch (ThreadDeath death) {
            throw new ScriptError("The task was artificially terminated.", death);
        } catch (ScriptError e) {
            throw e;
        } catch (CommandException e) {
            throw new ScriptError("Failed to execute command.", e);
        } catch (InterruptedException e) {
            throw new ScriptError("Task progress was interrupted.", e);
        } catch (Throwable e) {
            throw new ScriptError("An unknown error occurred while running task.", e);
        }
        //</editor-fold>
    };
    private boolean closing;

    public void checkIn(Context context) { // we run this when a script takes control of a CPU slice
        this.runtime.checkIn(context.data().script);
    }

    public void pauseCheckIn(Context context) { // we run this when a script gives up control of a CPU slice
        this.runtime.pauseCheckIn(context.data().script);
    }

    public void submit(ScriptTask task) {
        this.queue.offer(task);
    }

    public ScriptTask pull() {
        return queue.poll();
    }

    public ScriptRuntime getRuntime(CommandSender sender) {
        return this.getRuntime();
    }

    public ScriptRuntime getRuntime() {
        if (closing) return null;
        if (runtime == null) {
            final ScriptManager manager = this.prepareScriptManager();
            final ScriptRuntime runtime = new ScriptRuntime(CraftScriptPlugin.storage, manager, this, Bukkit.getConsoleSender(), 0);
            return this.runtime = runtime;
        } else return runtime;
    }

    public ScriptManager getScriptManager() {
        if (closing) return null;
        return this.getRuntime().manager;
    }

    @Override
    public ScriptManager getScriptManager(CommandSender sender) {
        return this.getScriptManager();
    }

    @Override
    public void runScript(AbstractScript script, CommandSender sender) {
        this.runtime.run(script);
    }

    protected ScriptManager prepareScriptManager() {
        final ScriptManager manager = new ScriptManager(CraftScriptPlugin.plugin, ScriptLoader.BASIC);
        for (final Kind<?> kind : kinds) manager.registerKind(kind);
//        if (manager.getParser() instanceof SimpleScriptLoader loader) {
////            loader.addParsers(ItemQueryParser::new); todo items
//        }
        manager.loadScript(Libraries.MATH);
        manager.loadScript(Libraries.PARSER);
        manager.loadScript(Libraries.GLOBAL);
        manager.loadScript(Libraries.DEBUG);
        manager.loadScript(Libraries.REFLECTION);
        manager.loadScript(MINECRAFT);
        manager.setExecutor(executor);
        manager.setBackgroundExecutor(background);
        return manager;
    }

    public void close() {
        this.closing = true;
        try {
            this.runtime.close();
        } catch (Throwable error) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to shut down runtime safely.", error);
        } finally {
            this.runtime = null;
        }
    }

    public void checkIn() {
        this.getRuntime().checkIn();
    }

    public void emit(Event event) {
        this.getRuntime().manager.emit(event);
    }    private final BackgroundTaskExecutor background = (script, executable, context) -> {
        this.getRuntime().runTask(script, executable, context);
    };




}
