package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.script.ScriptManager;
import org.bukkit.command.CommandSender;

public interface ScriptController {

    ScriptManager getScriptManager(CommandSender sender);

    default AbstractScript getScript(CommandSender sender, String name) {
        final ScriptManager manager = this.getScriptManager(sender);
        return manager.getScript(name);
    }

    default AbstractScript[] getScripts(CommandSender sender) {
        return this.getScriptManager(sender).getScripts();
    }

    default void runScript(AbstractScript script, CommandSender sender) {
        final ScriptManager manager = this.getScriptManager(sender);
        manager.runScriptSafely(script, sender);
    }

}
