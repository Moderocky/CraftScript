package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ForStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableAssignmentStatement;

public class ForParser extends BasicParser {

    private VariableAssignmentStatement check;
    private Statement<?> then;

    @Override
    public boolean matches() {
        if (!input.startsWith("for ")) return false;
        int start = 4, original = start;
        do {
            final int space = input.indexOf(' ', start);
            if (space < 0) return false;
            if (start >= input.length()) return false;
            final String before, after;
            before = input.substring(original, space).trim();
            after = input.substring(space + 1).trim();
            start = space + 1;
            if (!(parent.parse(before) instanceof VariableAssignmentStatement check)) continue;
            this.check = check;
            this.then = parent.parse(after);
            if (then == null) continue;
            return true;
        } while (start < input.length());
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new ForStatement(check, then);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.check = null;
        this.then = null;
    }

}
