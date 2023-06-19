package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.utility.TaskExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.text;

public class ScriptManager implements Closeable {

    public static final TaskExecutor DISPATCHER = (context, task) -> {
        try {
            if (Bukkit.isPrimaryThread()) return task.get();
            else return Bukkit.getScheduler().callSyncMethod(context.manager().getPlugin(), task::get).get();
        } catch (ExecutionException | CommandException e) {
            throw new ScriptError("An unknown error occurred while running task.", e);
        } catch (InterruptedException e) {
            throw new ScriptError("Failed to run task.", e);
        }
    };
    protected final JavaPlugin plugin;
    protected final ScriptLoader loader;
    protected final Map<String, Script> scripts = new LinkedHashMap<>();
    protected final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    protected final Set<Kind<?>> kinds = new LinkedHashSet<>();
    protected final boolean test;
    protected TaskExecutor executor = DISPATCHER;

    public ScriptManager(JavaPlugin plugin, ScriptLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        this.test = plugin == null || Bukkit.getServer() == null;
    }

    public AbstractScript parseScript(String content) {
        return this.parseScript(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public AbstractScript parseScript(InputStream content) {
        try {
            return loader.parse(content);
        } catch (IOException ex) {
            throw new ScriptError("Error loading script content.", ex);
        }
    }

    public Script parseScript(String name, String content) {
        return this.parseScript(name, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public Script parseScript(String name, InputStream stream) {
        try {
            return loader.parse(name, stream);
        } catch (IOException ex) {
            throw new ScriptError("Error loading script content.", ex);
        }
    }

    public Script loadScript(String name, InputStream stream) {
        final Script script = this.parseScript(name, stream);
        return this.loadScript(script);
    }

    public Script loadScript(String name, String content) {
        return this.loadScript(name, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public Script loadScript(Script script) {
        synchronized (scripts) {
            this.scripts.put(script.name(), script);
        }
        return script;
    }

    public Script getScript(String name) {
        synchronized (scripts) {
            return scripts.get(name);
        }
    }

    public void deleteScript(String name) {
        synchronized (scripts) {
            this.scripts.remove(name);
        }
    }

    public void deleteScript(Script script) {
        synchronized (scripts) {
            this.scripts.values().removeIf(script::equals);
        }
    }

    public Object runScript(AbstractScript script, CommandSender source) {
        final Context context = new Context(source, this);
        Context.setLocalContext(context);
        try {
            return script.execute(context);
        } finally {
            Context.removeLocalContext();
        }
    }

    public Object runScriptSafely(AbstractScript script, CommandSender source) {
        try {
            return this.runScript(script, source);
        } catch (ScriptError error) {
            this.printError(error, source);
        } catch (ThreadDeath death) { // planned termination
            throw death;
        } catch (Throwable ex) {
            this.printError(new ScriptError("An unknown error occurred.", ex), source);
        }
        return null;
    }

    public Script[] getScripts() {
        return scripts.values().toArray(new Script[0]);
    }

    public void printError(ScriptError error, CommandSender sender) {
        if (this.isTest()) throw error;
        final String top;
        if (Context.getLocalContext() != null)
            top = "Script Error in '" + Context.getLocalContext().data().script.name() + "':";
        else top = "Script Error:";
        sender.sendMessage(Component.textOfChildren(text("!! ", NamedTextColor.WHITE).decorate(TextDecoration.BOLD),
            text(top, NamedTextColor.RED), Component.newline(), text(error.getMessage(), NamedTextColor.GRAY)));
    }

    public Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }

    @Override
    public void close() {
        synchronized (scripts) {
            this.scripts.clear();
        }
    }

    public void setExecutor(TaskExecutor executor) {
        this.executor = executor;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public boolean isTest() {
        return test;
    }

    public void registerKind(Kind<?> kind) {
        this.kinds.add(kind);
    }

    public Set<Kind<?>> getKinds() {
        return kinds;
    }

    private Command handle(MinecraftCommand command) {
        return new Command(command.label()) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                return command.onCommand(sender, this, commandLabel, args);
            }
        };
    }

    public boolean runCommand(Context context, String command) {
        final Set<MinecraftCommand> set = context.data().localCommands;
        if (!set.isEmpty()) {
            final String label;
            final String[] args;
            final int index = command.indexOf(' ');
            if (index < 1) {
                args = new String[0];
                label = command;
            } else {
                label = command.substring(0, index);
                args = command.substring(index + 1).split(" ");
            }
            for (final MinecraftCommand local : context.data().localCommands) {
                if (!local.label().equals(label)) continue;
                return local.onCommand(context.source(), this.handle(local), label, args);
            }
        }
        if (this.isTest()) {
            return false;
        }
        return (boolean) this.executeOnPrimary(context, () -> Bukkit.dispatchCommand(context.source(), command));
    }

    protected Object executeOnPrimary(Context context, Supplier<Object> supplier) {
        return executor.execute(context, supplier);
    }

}
