package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.CommandStatement;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.Statement;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class CommandParser extends BasicParser {

    private int start;

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
        if (input.indexOf('}') > input.indexOf('{')) {
            final List<InterpolationStatement> list = new ArrayList<>();
            do {
                final int open = input.indexOf('{', start), close = input.indexOf('}', open);
                if (open < 0 || close < 0) break;
                final String key = input.substring(open, close + 1);
                final Statement<?> statement = parent.parse(key);
                if (statement instanceof InterpolationStatement interpolation) list.add(interpolation);
                this.start = close;
            } while (true);
            return new CommandStatement(input.substring(1).trim(), list.toArray(new InterpolationStatement[0]));
        } else return new CommandStatement(input.substring(1).trim());
    }

    @Override
    public void close() throws ScriptError {
        this.start = 0;
        super.close();
    }

}
