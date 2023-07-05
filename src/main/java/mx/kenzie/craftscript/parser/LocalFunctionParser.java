package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.RunStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableStatement;

public class LocalFunctionParser extends BasicParser {

    private Statement<?> runnable;
    private Statement<?> variables;

    @Override
    public boolean matches() {
        int start = 0;
        do {
            final int space = input.indexOf(' ', start);
            final String before, after;
            if (space < 0) {
                before = input;
                after = null;
            } else {
                before = input.substring(0, space).trim();
                after = input.substring(space + 1).trim();
            }
            start = space + 1;
            this.runnable = parent.parse(before);
            if (!(runnable instanceof VariableStatement)) continue;
            if (after == null) return true;
            if (after.isEmpty()) continue;
            this.variables = parent.parse(after);
            if (variables == null) continue;
            return true;
        } while (start < input.length() && start > 2);
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new RunStatement(runnable, variables);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.runnable = null;
        this.variables = null;
    }

}
