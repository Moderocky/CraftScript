package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.RequireStatement;
import mx.kenzie.craftscript.statement.Statement;

import static mx.kenzie.craftscript.variable.VariableContainer.VAR_NAME;

public class RequireParser extends BasicParser {

    private String[] names;

    @Override
    public boolean matches() {
        if (!input.startsWith("require")) return false;
        if (input.length() < 9) return false;
        final int start = input.indexOf('['), end = input.lastIndexOf(']');
        if (start < 0 || end < 0) return false;
        final String string = input.substring(start + 1, end).trim();
        this.names = new String[0];
        if (string.isEmpty()) return true;
        this.names = string.split(" *, *");
        return true;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        for (final String name : names) {
            if (!VAR_NAME.matcher(name).matches())
                throw new ScriptError("The name '" + name + "' is not a valid variable identifier.");
        }
        return new RequireStatement(names);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.names = null;
    }

}
