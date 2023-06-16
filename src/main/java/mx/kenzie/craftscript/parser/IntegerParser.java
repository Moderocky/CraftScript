package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.LiteralStatement;
import mx.kenzie.craftscript.statement.Statement;

public class IntegerParser extends BasicParser {

    private Integer value;

    @Override
    public boolean matches() {
        try {
            this.value = Integer.valueOf(input);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new LiteralStatement(value != null ? value : 0);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.value = null;
    }

}
