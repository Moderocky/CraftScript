package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableStatement;

import static mx.kenzie.craftscript.variable.VariableContainer.VAR_NAME;

public class VariableParser extends BasicParser {

    private String key;

    @Override
    public boolean matches() {
        this.key = input.trim();
        if (key.isEmpty()) return false;
        return VAR_NAME.matcher(key).matches();
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new VariableStatement(key);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.key = null;
    }

}
