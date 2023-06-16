package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.LiteralStatement;
import mx.kenzie.craftscript.statement.Statement;

public class DoubleParser extends BasicParser {

    private Double value;

    @Override
    public boolean matches() {
        try {
            this.value = Double.valueOf(input);
            return true;
        } catch (NumberFormatException ex) {
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
