package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.FunctionStatement;
import mx.kenzie.craftscript.statement.Statement;

public class FunctionParser extends BasicParser {

    private Statement<?> block;

    @Override
    public boolean matches() {
        if (!input.startsWith("function ")) return false;
        if (input.length() < 10) return false;
        final String string = input.substring(9).trim();
        if (string.isEmpty()) return false;
        this.block = parent.parse(string);
        return block != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new FunctionStatement(block);
    }

    @Override
    public void close() throws ScriptError {
        this.block = null;
        super.close();
    }

}
