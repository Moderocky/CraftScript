package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.DoBlockParser;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptParser;
import mx.kenzie.craftscript.statement.DoStatement;
import mx.kenzie.craftscript.statement.Statement;

public class DoParser extends BasicParser {

    private Statement<?> object;
    private Statement<?> block;

    @Override
    public boolean matches() {
        if (!input.startsWith("do ")) return false;
        int start = 3, original = start;
        do {
            final int space = input.indexOf(' ', start);
            if (space < 0) return false;
            if (start >= input.length()) return false;
            final String before, after;
            before = input.substring(original, space).trim();
            after = input.substring(space + 1).trim();
            start = space + 1;
            this.object = parent.parse(before);
            if (object == null) continue;
            final ScriptParser parser = new DoBlockParser(parent);
            this.block = parser.parse(after);
            if (block == null) continue;
            return true;
        } while (start < input.length());
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new DoStatement(object, block);
    }

    @Override
    public void close() throws ScriptError {
        this.object = null;
        this.block = null;
        super.close();
    }

}
