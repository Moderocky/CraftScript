package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.KindStatement;
import mx.kenzie.craftscript.statement.Statement;

public class KindParser extends BasicParser {

    @Override
    public boolean matches() {
        if (!input.startsWith("#")) return false;
        return input.length() > 1;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new KindStatement(input.substring(1));
    }

}
