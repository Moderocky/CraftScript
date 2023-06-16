package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.CloseStatement;
import mx.kenzie.craftscript.statement.Statement;

public class CloseParser extends BasicParser {

    @Override
    public boolean matches() {
        return input.equals("}");
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new CloseStatement();
    }

}
