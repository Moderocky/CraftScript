package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.FunctionStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.VariableHelper;

public class FunctionParser extends BasicParser {

    private Statement<?> block;

    @Override
    public boolean matches() {
        if (!input.startsWith("function ")) return false;
        if (input.length() < 10) return false;
        final String string = input.substring(9).trim();
        if (string.isEmpty()) return false;
        final VariableHelper helper = VariableHelper.instance(), child = helper.clone();
        try {
            child.purge(); // outer variables aren't available in a function
            VariableHelper.local.set(child);
            this.block = parent.parse(string);
        } finally {
            VariableHelper.local.set(helper);
        }
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
