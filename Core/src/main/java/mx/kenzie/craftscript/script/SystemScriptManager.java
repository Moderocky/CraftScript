package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.Command;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.utility.BackgroundTaskExecutor;
import mx.kenzie.craftscript.utility.ScriptHelper;
import mx.kenzie.craftscript.utility.TaskExecutor;
import mx.kenzie.craftscript.variable.ConcurrentVariableContainer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SystemScriptManager implements ScriptManager<PrintStream> {

    public static final TaskExecutor DISPATCHER = (context, task) -> task.get();
    public static final BackgroundTaskExecutor BACKGROUND = (source, executable, context) -> {
        Context.setLocalContext(context);
        executable.execute(context);
        Context.removeLocalContext();
    };
    protected final ScriptLoader loader;
    protected final Map<String, AbstractScript> scripts = new LinkedHashMap<>();
    protected final Map<String, Object> globalVariables = ConcurrentVariableContainer.noParameters();
    protected final Set<Kind<?>> kinds = new LinkedHashSet<>();
    protected TaskExecutor executor = DISPATCHER;
    protected BackgroundTaskExecutor backgroundExecutor = BACKGROUND;

    public SystemScriptManager(ScriptLoader loader) {
        this.loader = loader;
    }

    @Override
    public AbstractScript parseScript(String content) {
        return this.parseScript(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public AbstractScript parseScript(InputStream content) {
        try {
            return loader.parse(content);
        } catch (IOException ex) {
            throw new ScriptError("Error loading script content.", ex);
        }
    }

    @Override
    public Script parseScript(String name, String content) {
        return this.parseScript(name, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public Script parseScript(String name, InputStream stream) {
        try {
            ScriptHelper.init(this);
            return loader.parse(name, stream);
        } catch (IOException ex) {
            throw new ScriptError("Error loading script content.", ex);
        } finally {
            ScriptHelper.tearDown();
        }
    }

    @Override
    public Script loadScript(String name, InputStream stream) {
        final Script script = this.parseScript(name, stream);
        return this.loadScript(script);
    }

    @Override
    public Script loadScript(String name, String content) {
        return this.loadScript(name, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public <Type extends AbstractScript> Type loadScript(Type script) {
        if (script instanceof AnonymousScript)
            throw new ScriptError("Unable to load anonymous script into environment.");
        synchronized (scripts) {
            this.scripts.put(script.name(), script);
        }
        return script;
    }

    @Override
    public AbstractScript getScript(String name) {
        synchronized (scripts) {
            return scripts.get(name);
        }
    }

    @Override
    public void deleteScript(String name) {
        synchronized (scripts) {
            this.scripts.remove(name);
        }
    }

    @Override
    public void deleteScript(AbstractScript script) {
        synchronized (scripts) {
            this.scripts.values().removeIf(script::equals);
        }
    }

    @Override
    public Object runScript(AbstractScript script, PrintStream source) {
        final Context<PrintStream> context = new Context<>(source, this);
        Context.setLocalContext(context);
        try {
            return script.execute(context);
        } catch (ScriptDeath death) {
            ScriptThread.step();
            throw death;
        } finally {
            Context.removeLocalContext();
        }
    }

    @Override
    public Object runScriptSafely(AbstractScript script, PrintStream source) {
        try {
            return this.runScript(script, source);
        } catch (ScriptDeath death) {
            throw death;
        } catch (ScriptError error) {
            this.printError(error, source);
        } catch (ThreadDeath death) { // planned termination
            throw death;
        } catch (Throwable ex) {
            this.printError(new ScriptError("An unknown error occurred.", ex), source);
        }
        return null;
    }

    @Override
    public AbstractScript[] getScripts() {
        return scripts.values().toArray(new AbstractScript[0]);
    }

    @Override
    public void printError(ScriptError error, PrintStream sender) {
        if (this.isTest()) throw error;
        if (!error.hasContext()) {
            sender.println("Script Error:" + error.getMessage());
        } else {
            sender.println("Script Error in '" + Context.getLocalContext().data().script.name() + "':" + error.getMessage());
            final Context<?> context = ((ScriptRuntimeError) error).getContext();
            if (context.data().line == null) return;
            sender.println("Line " + context.data().line + ": " + context.data().line.stringify());
        }
        sender.println(error);
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }

    @Override
    public void close() {
        synchronized (scripts) {
            this.scripts.clear();
        }
    }

    @Override
    public void setExecutor(TaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void setBackgroundExecutor(BackgroundTaskExecutor executor) {
        this.backgroundExecutor = executor;
    }

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public void registerKind(Kind<?> kind) {
        this.kinds.add(kind);
    }

    @Override
    public Set<Kind<?>> getKinds() {
        return kinds;
    }

    @Override
    public boolean runCommand(Context<?> context, String command) {
        final Set<Command<?>> set = context.data().localCommands;
        if (!set.isEmpty()) {
            final String label;
            final int index = command.indexOf(' ');
            if (index < 1) label = command;
            else label = command.substring(0, index);
            for (final Command<?> local : set) {
                if (!local.label().equals(label)) continue;
                return local.execute(context.getSource(), command).successful();
            }
        }
        return false;
    }

    @Override
    public Object executeOnPrimary(Context<?> context, Supplier<Object> supplier) {
        return executor.execute(context, supplier);
    }

    @Override
    public ScriptSourceParser getParser() {
        return loader;
    }

    @Override
    public void println(PrintStream sender, String line) {
        sender.println(line);
    }

}
