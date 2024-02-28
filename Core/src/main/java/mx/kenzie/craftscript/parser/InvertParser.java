package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.InvertStatement;
import mx.kenzie.craftscript.statement.Statement;

public class InvertParser extends BasicParser {

    private Statement<?> check;

    @Override
    public boolean matches() {
        if (!input.startsWith("!")) return false;
        final String string = input.substring(1).trim();
        this.check = parent.parse(string);
        return check != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new InvertStatement(check);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.check = null;
    }

}
