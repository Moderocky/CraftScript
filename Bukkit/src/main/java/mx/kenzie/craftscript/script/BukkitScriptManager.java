package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.craftscript.emitter.Event;
import mx.kenzie.craftscript.emitter.EventListener;
import mx.kenzie.craftscript.emitter.ListenerList;
import mx.kenzie.craftscript.kind.EventKind;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.listener.GameEventListener;
import mx.kenzie.craftscript.utility.BackgroundTaskExecutor;
import mx.kenzie.craftscript.utility.ScriptHelper;
import mx.kenzie.craftscript.utility.TaskExecutor;
import mx.kenzie.craftscript.variable.ConcurrentVariableContainer;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.text;

public class BukkitScriptManager implements ScriptManager<CommandSender> {

    public static final TaskExecutor DISPATCHER = (context, task) -> {
        try {
            if (Bukkit.isPrimaryThread()) return task.get();
            else
                return Bukkit.getScheduler().callSyncMethod(((BukkitScriptManager) context.manager()).getPlugin(), task::get).get();
        } catch (ExecutionException | CommandException e) {
            throw new ScriptError("An unknown error occurred while running task.", e);
        } catch (InterruptedException e) {
            throw new ScriptError("Failed to run task.", e);
        }
    };
    public static final BackgroundTaskExecutor BACKGROUND = (source, executable, context) -> {
        Context.setLocalContext(context);
        executable.execute(context);
        Context.removeLocalContext();
    };
    protected final JavaPlugin plugin;
    protected final ScriptLoader loader;
    protected final Map<String, AbstractScript> scripts = new LinkedHashMap<>();
    protected final Map<String, Object> globalVariables = ConcurrentVariableContainer.noParameters();
    protected final Map<NamespacedKey, ListenerList> listenerMap = new ConcurrentHashMap<>();
    protected final Set<Kind<?>> kinds = new LinkedHashSet<>();
    protected final boolean test;
    protected final GameEventListener listener = new GameEventListener(this);
    protected TaskExecutor executor = DISPATCHER;
    protected BackgroundTaskExecutor backgroundExecutor = BACKGROUND;

    public BukkitScriptManager(JavaPlugin plugin, ScriptLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        this.test = plugin == null || Bukkit.getServer() == null;
        if (!test) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::registerListeners);
    }

    protected void registerListeners() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    protected void unregisterListeners() {
        GenericGameEvent.getHandlerList().unregister(listener);
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
        final AbstractScript script;
        synchronized (scripts) {
            script = this.scripts.remove(name);
        }
        if (script != null) this.unregisterListeners(script);
    }

    @Override
    public void deleteScript(AbstractScript script) {
        synchronized (scripts) {
            this.scripts.values().removeIf(script::equals);
        }
        this.unregisterListeners(script);
    }

    @Override
    public Object runScript(AbstractScript script, CommandSender source) {
        final Context context = new Context(source, this);
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
    public Object runScriptSafely(AbstractScript script, CommandSender source) {
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
    public void printError(ScriptError error, CommandSender sender) {
        if (this.isTest()) throw error;
        if (!error.hasContext()) {
            sender.sendMessage(Component.textOfChildren(text("!! ", NamedTextColor.WHITE).decorate(TextDecoration.BOLD), text("Script Error:", NamedTextColor.RED), Component.newline(), text(error.getMessage(), NamedTextColor.GRAY)));
            return;
        }
        final Context<?> context = ((ScriptRuntimeError) error).getContext();
        sender.sendMessage(Component.textOfChildren(text("!! ", NamedTextColor.WHITE).decorate(TextDecoration.BOLD), text("Script Error in '" + Context.getLocalContext().data().script.name() + "':", NamedTextColor.RED), Component.newline(), text(error.getMessage(), NamedTextColor.GRAY)));
        if (context.data().line == null) return;
        sender.sendMessage(text("Line " + context.data().line + ": " + context.data().line.stringify(), NamedTextColor.GRAY));
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }

    @Override
    public void close() {
        this.unregisterListeners();
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
        return test;
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
        final Set<MinecraftCommand> set = (Set<MinecraftCommand>) (Set) context.data().localCommands;
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
            for (final MinecraftCommand local : set) {
                if (!local.label().equals(label)) continue;
                return local.onCommand(context.getSource(), this.handle(local), label, args);
            }
        }
        if (this.isTest()) {
            return false;
        }
        return (boolean) this.executeOnPrimary(context, () -> Bukkit.dispatchCommand(context.getSource(), command));
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
    public void println(CommandSender sender, String line) {
        sender.sendMessage(line);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private Command handle(MinecraftCommand command) {
        return new Command(command.label()) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                return command.onCommand(sender, this, commandLabel, args);
            }
        };
    }

    public void registerListener(EventListener listener) {
        final NamespacedKey key = listener.event();
        this.listenerMap.putIfAbsent(key, new ListenerList());
        final ListenerList listeners = listenerMap.get(key);
        listeners.add(listener);
    }

    public void unregisterListener(EventListener listener) {
        final NamespacedKey key = listener.event();
        final ListenerList listeners = listenerMap.get(key);
        if (listeners == null) return;
        listeners.remove(listener);
        if (listeners.isEmpty()) listenerMap.remove(key);
    }

    public void unregisterListeners(AbstractScript script) {
        for (final EventListener listener : this.getListeners()) {
            if (listener.getDetails().data().script == script) this.unregisterListener(listener);
        }
    }

    public boolean hasListeners(AbstractScript script) {
        for (final ListenerList value : listenerMap.values()) {
            for (final EventListener listener : value) {
                if (listener.getDetails().data().script == script) return true;
            }
        }
        return false;
    }

    public Collection<EventListener> getListeners() {
        final List<EventListener> listeners = new LinkedList<>();
        for (final ListenerList value : listenerMap.values()) listeners.addAll(value);
        return listeners;
    }

    public void emit(Event event) {
        final NamespacedKey key = event.key();
        if (!listenerMap.containsKey(key)) return;
        for (final EventListener listener : listenerMap.get(key)) {
            if (!listener.isRelevant(event)) continue;
            final EventListener.Details details = listener.getDetails();
            try {
                final Context.Data data = details.data().clone();
                final Context<CommandSender> context = new Context<>(details.owner(), this, new VariableContainer(), data);
                context.variables().put("event", new Wrapper<>(event, new EventKind()));
                BACKGROUND.execute(data.script, listener, context);
            } catch (ThreadDeath death) {
                throw death;
            } catch (Throwable ex) {
                this.printError(new ScriptError("Error in event listener.", ex), details.owner());
            }
        }
    }

}
