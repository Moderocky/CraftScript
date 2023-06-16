package mx.kenzie.craftscript;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.craftscript.kind.Kind;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static net.kyori.adventure.text.Component.text;

public class ScriptManager implements Closeable {

    protected final JavaPlugin plugin;
    protected final ScriptLoader loader;
    protected final Map<String, Script> scripts = new LinkedHashMap<>();
    protected final Set<Kind<?>> kinds = new LinkedHashSet<>();
    protected final boolean test;

    public ScriptManager(JavaPlugin plugin, ScriptLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        this.test = plugin == null || Bukkit.getServer() == null;
    }

    public Script loadScript(String name, InputStream stream) {
        try {
            final Script script = loader.load(name, stream);
            synchronized (scripts) {
                this.scripts.put(script.name(), script);
            }
            return script;
        } catch (IOException ex) {
            throw new ScriptError("Error loading script content.", ex);
        }
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

    public boolean runScript(Script script, CommandSender source) {
        final Context context = new Context(source, this);
        return script.execute(context);
    }

    public void printError(ScriptError error, CommandSender sender) {
        if (this.isTest()) {
            error.printStackTrace();
            return;
        }
        final String top;
        if (Context.getLocalContext() != null) top =
            "Script Error in '" +
                Context.getLocalContext().data().script.name() + "':";
        else top = "Script Error:";
        sender.sendMessage(Component.textOfChildren(
            text("!! ", NamedTextColor.WHITE).decorate(TextDecoration.BOLD),
            text(top, NamedTextColor.RED),
            Component.newline(),
            text(error.getMessage(), NamedTextColor.GRAY)
        ));
    }

    @Override
    public void close() {
        synchronized (scripts) {
            this.scripts.clear();
        }
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
        try {
            if (Bukkit.isPrimaryThread()) return Bukkit.dispatchCommand(context.source(), command);
            else return Bukkit.getScheduler()
                .callSyncMethod(context.manager().getPlugin(), () -> Bukkit.dispatchCommand(context.source(), command))
                .get();
        } catch (ExecutionException | CommandException e) {
            throw new ScriptError("An unknown error occurred while running '" + command + "'.", e);
        } catch (InterruptedException e) {
            throw new ScriptError("Failed to run '" + command + "'.", e);
        }
    }

    public String toString(Object object) {
        if (object instanceof Player player) return player.getName();
        if (object instanceof CommandSender player) return player.getName();
        if (object instanceof Block block) return block.getType().getKey().toString();
        if (object instanceof BlockData data) return data.getAsString(true);
        if (object instanceof Location location) return location.getX() + " " + location.getY() + " " + location.getZ();
        if (object instanceof Vector location) return location.getX() + " " + location.getY() + " " + location.getZ();
        return Objects.toString(object);
    }

}
