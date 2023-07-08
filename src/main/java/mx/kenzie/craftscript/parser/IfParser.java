package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ChoiceBlockStatement;
import mx.kenzie.craftscript.statement.IfElseStatement;
import mx.kenzie.craftscript.statement.IfStatement;
import mx.kenzie.craftscript.statement.Statement;

public class IfParser extends BasicParser {

    protected Statement<?> check;
    protected Statement<?> then;

    @Override
    public boolean matches() {
        if (!input.startsWith("if ")) return false;
        return this.parse(3);
    }

    protected boolean parse(int begin) {
        int start = begin;
        do {
            final int space = input.indexOf(' ', start);
            if (space < 0) return false;
            if (start >= input.length()) return false;
            final String before, after;
            before = input.substring(begin, space).trim();
            after = input.substring(space + 1).trim();
            start = space + 1;
            this.check = parent.parse(before);
            if (check == null) continue;
            this.then = parent.parse(after);
            if (then == null) continue;
            return true;
        } while (start < input.length());
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        if (then instanceof ChoiceBlockStatement choice) return new IfElseStatement(check, choice);
        return new IfStatement(check, then);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.check = null;
        this.then = null;
    }

}
