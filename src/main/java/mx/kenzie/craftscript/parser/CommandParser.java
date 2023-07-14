package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.*;
import mx.kenzie.craftscript.utility.Interpolator;
import org.bukkit.Bukkit;

public class CommandParser extends BasicParser {

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        if (!input.startsWith("/")) return false;
        final int space = input.indexOf(' ', 2);
        final String label;
        if (space < 0) label = input.substring(1).trim();
        else label = input.substring(1, space).trim();
        if (Bukkit.getServer() == null) return true;
        if (Bukkit.getCommandMap().getCommand(label) != null) return true;
        throw new ScriptError("Line " + parent.getLine() + ": '/" + label + "' is not a known command.");
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        final String content = input.substring(1).trim();
        final Object[] parts = new Interpolator(content, parent).interpolations();
        if (parts.length == 0 || parts.length == 1 && parts[0] instanceof String) return new LiteralStringStatement(content);
        return new CommandStatement(content, parts);
    }

}
