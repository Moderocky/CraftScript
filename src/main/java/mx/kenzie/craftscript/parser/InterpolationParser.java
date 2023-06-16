package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.Statement;

public class InterpolationParser extends BasicParser {

    private String statement;
    private Statement<?> assignment;

    @Override
    public boolean matches() {
        if (input.length() < 3) return false;
        if (!input.startsWith("{")) return false;
        if (!input.endsWith("}")) return false;
        this.statement = input.substring(1, input.length() - 1).trim();
        if (statement.isEmpty()) return false;
        this.assignment = parent.parse(statement);
        return assignment != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new InterpolationStatement(statement, assignment);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.assignment = null;
        this.statement = null;
    }

}
