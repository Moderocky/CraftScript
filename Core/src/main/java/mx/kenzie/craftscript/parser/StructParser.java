package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.*;

public class StructParser extends BasicParser {

    private BlockStatement block;

    @Override
    public boolean matches() {
        if (!input.startsWith("struct ")) return false;
        final String string = input.substring(6).trim();
        final int line = parent.getLine();
        final Statement<?> statement = parent.parse(string);
        if (!(statement instanceof BlockStatement block)) throw new ScriptError(
            "The structure in line " + line + " is not followed by a block opener '{'");
        this.block = block;
        for (final Statement<?> child : block.statements()) {
            if (child instanceof VariableAssignmentStatement) continue;
            else if (child instanceof LineStatement wrapper
                && wrapper.statement() instanceof VariableAssignmentStatement) continue;
            final Statement<?> real;
            if (child instanceof LineStatement wrapper) real = wrapper.statement();
            else real = child;
            throw new ScriptError(
                "The structure in line " + line + " contains a non-variable assignment element '" + real.getClass()
                    .getSimpleName() + "'.");
        }
        return true;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new StructStatement(block);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.block = null;
    }

}
