package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ElseStatement;
import mx.kenzie.craftscript.statement.Statement;

public class ElseParser extends BasicParser {

    @Override
    public boolean matches() {
        if (!input.startsWith("}")) return false;
        if (!input.contains("else")) return false;
        final int split = input.indexOf("else");
        final String before, after;
        before = input.substring(0, split).trim();
        after = input.substring(split + 4).trim();
        if (!before.equals("}")) return false;
        return after.equals("{");
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new ElseStatement();
    }

}
