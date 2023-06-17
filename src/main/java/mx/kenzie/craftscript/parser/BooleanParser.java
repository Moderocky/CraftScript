package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.LiteralStatement;
import mx.kenzie.craftscript.statement.Statement;

public class BooleanParser extends BasicParser {

    @Override
    public boolean matches() {
        return input.equals("true") || input.equals("false");
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new LiteralStatement("true".equals(input));
    }

}
