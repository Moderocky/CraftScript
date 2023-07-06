package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ScriptStatement;
import mx.kenzie.craftscript.statement.Statement;

public class ScriptParser extends BasicParser {

    @Override
    public boolean matches() {
        if (!input.endsWith(".script")) return false;
        if (input.length() < 8) return false;
        return !input.contains(" ");
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new ScriptStatement(input);
    }

}
