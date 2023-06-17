package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.RunStatement;
import mx.kenzie.craftscript.statement.Statement;

public class RunParser extends BasicParser {

    private Statement<?> check;
    private Statement<?> then;

    @Override
    public boolean matches() {
        if (!input.startsWith("run ")) return false;
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
            this.check = parent.parse(before);
            if (check == null) continue;
            if (after == null) return true;
            if (after.isEmpty()) continue;
            this.then = parent.parse(after);
            if (then == null) continue;
            return true;
        } while (start < input.length() && start > 2);
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new RunStatement(check, then);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.check = null;
        this.then = null;
    }

}
