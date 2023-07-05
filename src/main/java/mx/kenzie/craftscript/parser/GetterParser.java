package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.GetterStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.variable.VariableContainer;

public class GetterParser extends BasicParser {

    private Statement<?> assignment;
    private String property;

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        if (!input.endsWith("]")) return false;
        final int index = input.lastIndexOf('[');
        if (index < 0) return false;
        final String label = input.substring(0, index).trim();
        this.property = input.substring(index + 1, input.length() - 1).trim();
        if (label.isEmpty()) return false;
        if (property.isEmpty()) return false;
        if (property.contains("=")) return false;
        if (!VariableContainer.VAR_NAME.matcher(property).matches()) return false;
        this.assignment = parent.parse(label);
        return assignment != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new GetterStatement(assignment, property);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.assignment = null;
        this.property = null;
    }

}
