package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.TypeStatement;
import mx.kenzie.craftscript.statement.VariableAssignmentStatement;

import static mx.kenzie.craftscript.variable.UnknownObject.TYPE_NAME;

public class TypeParser extends BasicParser {

    private String name;
    private BlockStatement block;

    @Override
    public boolean matches() {
        if (!input.startsWith("type ")) return false;
        int start = 5, original = start;
        do {
            final int space = input.indexOf(' ', start);
            if (space < 0) return false;
            if (start >= input.length()) return false;
            final String before, after;
            before = input.substring(original, space).trim();
            after = input.substring(space + 1).trim();
            start = space + 1;
            if (!TYPE_NAME.matcher(before).matches()) return false;
            this.name = before;
            final int line = parent.getLine();
            final Statement<?> statement = parent.parse(after);
            if (!(statement instanceof BlockStatement block)) continue;
            this.block = block;
            for (final Statement<?> child : block.statements()) {
                if (!(child instanceof VariableAssignmentStatement)) throw new ScriptError(
                    "The type block in line " + line + " contains a non-variable assignment element.");
            }
            return true;
        } while (start < input.length() && start > 5);
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new TypeStatement(name, block);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.block = null;
        this.name = null;
    }

}
