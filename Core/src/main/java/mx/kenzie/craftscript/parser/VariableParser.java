package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableStatement;
import mx.kenzie.craftscript.utility.VariableHelper;

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
        if (!VariableHelper.instance().isKnown(key))
            this.parent.warn("The variable '" + key + "' may not exist.");
        return new VariableStatement(key);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.key = null;
    }

}
