package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.RunStatement;
import mx.kenzie.craftscript.statement.Statement;

public class RunParser extends BasicParser {

    private Statement<?> runnable;
    private Statement<?> variables;

    @Override
    public boolean matches() {
        if (!input.startsWith("run ")) return false;
        if (input.startsWith("run function ")) { // special case because it wants to parse `run %variable% %statement%
            final String string = input.substring(4).trim();
            this.runnable = parent.parse(string);
            this.variables = null;
            return runnable != null;
        }
        int start = 4, begin = start;
        do {
            final int space = input.indexOf(' ', start);
            final String before, after;
            if (space < 0) {
                before = input.substring(begin).trim();
                after = null;
            } else {
                before = input.substring(begin, space).trim();
                after = input.substring(space + 1).trim();
            }
            start = space + 1;
            this.runnable = parent.parse(before);
            if (runnable == null) continue;
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
