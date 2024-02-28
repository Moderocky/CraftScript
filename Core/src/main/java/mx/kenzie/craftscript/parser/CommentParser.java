package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;

public class CommentParser extends BasicParser {

    private Statement<?> statement;

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        int start = 0;
        do {
            final int comment = input.indexOf("//", start);
            if (comment < 0) return false;
            final String before = input.substring(0, comment).trim();
            if (before.isEmpty()) return true;
            this.statement = parent.parse(before);
            if (statement != null) return true;
            start = comment + 2;
        } while (start < input.length());
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return statement;
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.statement = null;
    }

}
