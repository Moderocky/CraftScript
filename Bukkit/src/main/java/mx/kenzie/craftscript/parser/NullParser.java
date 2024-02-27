package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.NullStatement;
import mx.kenzie.craftscript.statement.Statement;

public class NullParser extends BasicParser {

    @Override
    public boolean matches() {
        return input.equals("null");
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new NullStatement();
    }

}
