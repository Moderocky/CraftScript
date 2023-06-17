package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.AssertStatement;
import mx.kenzie.craftscript.statement.Statement;

public class AssertParser extends BasicParser {

    private Statement<?> check;

    @Override
    public boolean matches() {
        if (!input.startsWith("assert ")) return false;
        final String string = input.substring(7).trim();
        if (string.isEmpty()) return false;
        this.check = parent.parse(string);
        return check != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new AssertStatement(check);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.check = null;
    }

}
