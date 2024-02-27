package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableAssignmentStatement;

import static mx.kenzie.craftscript.variable.VariableContainer.VAR_NAME;

public class VariableAssignmentParser extends BasicParser {

    private String key;
    private Statement<?> assignment;

    @Override
    public boolean matches() {
        if (input.length() < 3) return false;
        final int index = input.indexOf('=');
        if (index < 1) return false;
        this.key = input.substring(0, index).trim();
        final String end = input.substring(index + 1).trim();
        if (!VAR_NAME.matcher(key).matches()) return false;
        this.assignment = parent.parse(end);
        return assignment != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new VariableAssignmentStatement(key, assignment);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.key = null;
        this.assignment = null;
    }

}
