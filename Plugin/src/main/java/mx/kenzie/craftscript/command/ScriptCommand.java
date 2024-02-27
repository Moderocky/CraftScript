package mx.kenzie.craftscript.command;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.centurion.CommandResult;
import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.craftscript.CraftScriptPlugin;
import mx.kenzie.craftscript.disk.ScriptStorage;
import mx.kenzie.craftscript.environment.ScriptController;
import mx.kenzie.craftscript.environment.ScriptRuntime;
import mx.kenzie.craftscript.script.*;
import mx.kenzie.craftscript.utility.Warning;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static net.kyori.adventure.text.Component.text;

public class ScriptCommand extends MinecraftCommand {

    public static final ColorProfile COLOR_PROFILE = ScriptArgument.COLOR_PROFILE;
    static final ScriptNameArgument NAME = new ScriptNameArgument();
    static final ScriptArgument SCRIPT = new ScriptArgument(CraftScriptPlugin.scripts);
    private final ScriptController controller = CraftScriptPlugin.scripts;
    private final CraftScriptPlugin plugin;

    public ScriptCommand(CraftScriptPlugin plugin) {
        super("Manage your scripts.");
        this.plugin = plugin;
    }

    public static Component prettyPrint(String url) {
        final ColorProfile profile = COLOR_PROFILE;
        final TextComponent.Builder text = text();
        var builder = new StringBuilder();
        boolean control = false;
        for (char c : url.toCharArray()) {
            switch (c) {
                case ':', '/', '.', '?', '#', ',', '&', '=':
                    if (!control) {
                        if (!builder.isEmpty()) text.append(Component.text(builder.toString(), profile.highlight()));
                        builder = new StringBuilder();
                        control = true;
                    }
                    builder.append(c);
                    break;
                default:
                    if (control) {
                        if (!builder.isEmpty()) text.append(Component.text(builder.toString(), profile.pop()));
                        builder = new StringBuilder();
                        control = false;
                    }
                    builder.append(c);
                    break;
            }
        }
        if (!builder.isEmpty())
            text.append(Component.text(builder.toString(), control ? profile.pop() : profile.highlight()));
        return text.build().clickEvent(ClickEvent.openUrl(url));
    }

    @Override
    public MinecraftCommand.MinecraftBehaviour create() {
        return this.command("script", "craftscript", "cs").permission("craftscript.command.script", PermissionDefault.TRUE).arg("run", SCRIPT, this::run).description("Run a script.").arg("stop", SCRIPT, this::stop).description("Terminates a script immediately while it is running.").arg("load", NAME, this::load).description("Parses and loads a script file from the disk.").arg("unload", SCRIPT, this::unload).description("Unloads a script.").arg("reload", NAME, this::reload).description("Re-parses and loads a script from its file.").arg("edit", NAME, this::upload).description("Edit or upload a script to the server.").arg("download", NAME, this::download).description("Download a script file from the server.").arg("delete", NAME, this::delete).description("Delete a script file from the server.").arg("debug", SCRIPT, this::debug).description("Print the interpreted structure of a script.").arg("list", this::list).description("Lists all your loaded scripts and available files.").arg("reset", this::reset).description("Terminates all scripts, resets your script controller.");
    }

    @Override
    protected ColorProfile getProfile() {
        return COLOR_PROFILE;
    }

    private CommandResult upload(CommandSender sender, Arguments arguments) {
        final String name = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        final ScriptManager manager = controller.getScriptManager(sender);
        if (manager.getScript(name) != null) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.must_unload", "You must unload '%s' before editing it.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        } else if (!ScriptStorage.isValidScriptName(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.invalid_name", "The text '%s' is not a valid script name.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        final ScriptStorage storage = controller.getRuntime(sender).getStorage();
        storage.createScript(name);
        if (!storage.scriptExists(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.io_error", "Error occurred preparing '%s'.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        final String url = CraftScriptPlugin.upload(name, storage);
        //<editor-fold defaultstate="collapsed" desc="Message">
        final Component link = prettyPrint(url).hoverEvent(text("Click to Upload", profile.light()));
        sender.sendMessage(Component.textOfChildren(text("Upload your script content at:", profile.dark()), Component.newline(), link, Component.newline(), text("This link will expire after after ", profile.dark()), text(5, profile.highlight()), text(" minutes.", profile.dark())));
        //</editor-fold>
        return CommandResult.PASSED;
    }

    private CommandResult reset(CommandSender sender, Arguments arguments) {
        if (!(sender instanceof Player)) return CommandResult.LAPSE;
        final ColorProfile profile = this.getProfile();
        //<editor-fold defaultstate="collapsed" desc="Reset Message">
        sender.sendMessage(Component.translatable("script.hard_reset", "Resetting all scripts to their default state.").color(profile.dark()));
        //</editor-fold>
        this.controller.close();
        this.controller.getRuntime(sender);
        //<editor-fold defaultstate="collapsed" desc="Reset Message">
        sender.sendMessage(Component.textOfChildren(Component.translatable("script.hard_reset_done", "Your runtime has been closed.").color(profile.dark())));
        //</editor-fold>
        return CommandResult.PASSED;
    }

    private CommandResult list(CommandSender sender, Arguments arguments) {
        final ColorProfile profile = this.getProfile();
        final ScriptManager manager = controller.getScriptManager(sender);
        final AbstractScript[] scripts = manager.getScripts();
        final Collection<String> names = controller.getRuntime(sender).getStorage().knownScripts();
        //<editor-fold defaultstate="collapsed" desc="Message">
        if (scripts.length > 0)
            sender.sendMessage(Component.translatable("script.loaded_scripts", "Loaded Scripts").color(profile.dark()));
        for (final AbstractScript script : scripts) {
            if (script instanceof LibraryScript) continue;
            names.remove(script.name());
            sender.sendMessage(Component.textOfChildren(text(" • ", profile.pop()), text(script.name(), profile.highlight())).hoverEvent(text("Click to Run")).clickEvent(ClickEvent.runCommand("/script run " + script.name())));
        }
        if (!names.isEmpty())
            sender.sendMessage(Component.translatable("script.unloaded_scripts", "Unloaded Scripts").color(profile.dark()));
        for (final String name : names) {
            sender.sendMessage(Component.textOfChildren(text(" • ", profile.pop()), text(name, profile.highlight())).hoverEvent(text("Click to Load")).clickEvent(ClickEvent.runCommand("/script reload " + name)));
        }
        //</editor-fold>
        return CommandResult.PASSED;
    }

    private CommandResult delete(CommandSender sender, Arguments arguments) {
        final String name = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        if (!ScriptStorage.isValidScriptName(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.invalid_name", "The text '%s' is not a valid script name.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        final ScriptManager manager = controller.getScriptManager(sender);
        if (manager.getScript(name) != null) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.must_unload", "You must unload '%s' before editing it.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        final ScriptStorage storage = controller.getRuntime(sender).getStorage();
        if (!storage.scriptExists(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.no_file", "There is no script named '%s'.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        if (!storage.deleteScript(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.cannot_delete", "Unable to delete script '%s'.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
        }
        return CommandResult.PASSED;
    }

    private CommandResult download(CommandSender sender, Arguments arguments) {
        final String name = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        if (!ScriptStorage.isValidScriptName(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.invalid_name", "The text '%s' is not a valid script name.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        final ScriptStorage storage = controller.getRuntime(sender).getStorage();
        if (!storage.scriptExists(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.no_file", "There is no script named '%s'.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        final String url = CraftScriptPlugin.download(name, storage);
        //<editor-fold defaultstate="collapsed" desc="Message">
        final Component link = prettyPrint(url).hoverEvent(text("Click to Download", profile.light()));
        sender.sendMessage(Component.textOfChildren(text("Your script is available to download at:", profile.dark()), Component.newline(), link, Component.newline(), text("This link will expire after after ", profile.dark()), text(5, profile.highlight()), text(" minutes.", profile.dark())));
        //</editor-fold>
        return CommandResult.PASSED;
    }

    private CommandResult load(CommandSender sender, Arguments arguments) {
        final String name = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        if (!ScriptStorage.isValidScriptName(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.invalid_name", "The text '%s' is not a valid script name.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        final ScriptStorage storage = controller.getRuntime(sender).getStorage();
        if (!storage.scriptExists(name)) {
            //<editor-fold defaultstate="collapsed" desc="Failure Message">
            sender.sendMessage(Component.translatable("script.no_file", "There is no script named '%s'.", text(name, profile.highlight())).color(profile.dark()));
            //</editor-fold>
            return CommandResult.PASSED;
        }
        //<editor-fold defaultstate="collapsed" desc="Load Message">
        sender.sendMessage(Component.translatable("script.load", "Loading '%s'.", text(name, profile.highlight())).color(profile.dark()));
        //</editor-fold>
        Bukkit.getScheduler().runTaskAsynchronously(CraftScriptPlugin.plugin, () -> {
            final ScriptManager manager = this.controller.getScriptManager(sender);
            try (InputStream stream = storage.openScript(name)) {
                manager.loadScript(name, stream);
                //<editor-fold defaultstate="collapsed" desc="Success Message">
                sender.sendMessage(Component.textOfChildren(Component.translatable("script.loaded", "Loaded '%s'.", text(name, profile.highlight())).color(profile.dark()), Component.newline(), text("Click to Run", profile.highlight()).hoverEvent(text("/script run " + name)).clickEvent(ClickEvent.runCommand("/script run " + name))));
                //</editor-fold>
            } catch (ScriptError error) {
                //<editor-fold defaultstate="collapsed" desc="Failure Message">
                sender.sendMessage(Component.translatable("script.parse_error", "Error while parsing '%s'.", text(name, profile.highlight())).color(profile.dark()));
                sender.sendMessage(text(error.getMessage(), profile.light()));
                //</editor-fold>
            } catch (IOException ex) {
                //<editor-fold defaultstate="collapsed" desc="Failure Message">
                sender.sendMessage(Component.translatable("script.no_file", "There is no script named '%s'.", text(name, profile.highlight())).color(profile.dark()));
                //</editor-fold>
            }
            final Warning[] warnings = manager.getParser().warnings().toArray(new Warning[0]);
            if (warnings.length < 1) return;
            //<editor-fold defaultstate="collapsed" desc="Warning Messages">
            sender.sendMessage(Component.textOfChildren(Component.newline(), Component.translatable("script.warnings", "Reported %s Warnings:", text(warnings.length, profile.highlight())).color(profile.dark())));
            for (final Warning warning : warnings) {
                sender.sendMessage(Component.textOfChildren(Component.translatable("script.warning_message", "Line %s: %s", text(warning.line(), profile.highlight()), text(warning.message(), profile.light())).color(profile.dark())));
            }
            sender.sendMessage(Component.newline());
            //</editor-fold>
        });
        return CommandResult.PASSED;
    }

    private CommandResult reload(CommandSender sender, Arguments arguments) {
        final String name = arguments.get(0);
        final AbstractScript script = controller.getScriptManager(sender).getScript(name);
        if (script != null) this.unload(sender, Arguments.of(script));
        return this.load(sender, Arguments.of(name));
    }

    private CommandResult unload(CommandSender sender, Arguments arguments) {
        final AbstractScript script = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        if (this.controller.getRuntime(sender).terminate(script)) {
            //<editor-fold defaultstate="collapsed" desc="Stop Message">
            sender.sendMessage(Component.translatable("script.stopped", "Terminated '%s'.", text(script.name(), profile.highlight())).color(profile.dark()));
            //</editor-fold>
        }
        //<editor-fold defaultstate="collapsed" desc="Unload Message">
        sender.sendMessage(Component.translatable("script.unload", "Unloading '%s'.", text(script.name(), profile.highlight())).color(profile.dark()));
        //</editor-fold>
        this.controller.getScriptManager(sender).deleteScript(script);
        return CommandResult.PASSED;
    }

    private CommandResult run(CommandSender sender, Arguments arguments) {
        final Script script = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        //<editor-fold defaultstate="collapsed" desc="Run Message">
        sender.sendMessage(Component.translatable("script.run", "Running '%s'.", text(script.name(), profile.highlight())).color(profile.dark()));
        //</editor-fold>
        try {
            this.controller.runScript(script, sender);
        } catch (ScriptError error) {
            //<editor-fold defaultstate="collapsed" desc="Error Message">
            sender.sendMessage(Component.textOfChildren(Component.translatable("script.error", "Error starting '%s':", text(script.name(), profile.highlight())).color(profile.dark()), Component.newline(), text(error.getMessage(), profile.light())));
            //</editor-fold>
        }
        return CommandResult.PASSED;
    }

    private CommandResult stop(CommandSender sender, Arguments arguments) {
        final Script script = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        final ScriptRuntime runtime = this.controller.getRuntime(sender);
        final boolean result = runtime.terminate(script);
        if (result) {
            //<editor-fold defaultstate="collapsed" desc="Stop Message">
            sender.sendMessage(Component.translatable("script.stopped", "Terminated '%s'.", text(script.name(), profile.highlight())).color(profile.dark()));
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="Not Running Message">
            sender.sendMessage(Component.translatable("script.not_running", "The script '%s' was not running.", text(script.name(), profile.highlight())).color(profile.dark()));
            //</editor-fold>
        }
        return CommandResult.PASSED;
    }

    private CommandResult debug(CommandSender sender, Arguments arguments) {
        final AbstractScript script = arguments.get(0);
        final ColorProfile profile = this.getProfile();
        //<editor-fold defaultstate="collapsed" desc="Unload Message">
        sender.sendMessage(Component.textOfChildren(Component.text(script.name(), profile.dark())));
        sender.sendMessage(script.prettyPrint(profile));
        //</editor-fold>
        return CommandResult.PASSED;
    }

}
