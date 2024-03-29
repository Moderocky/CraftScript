package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.DoBlockParser;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.LocalFunctionStatement;
import mx.kenzie.craftscript.statement.LocalKeywordStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableStatement;

public class LocalFunctionParser extends BasicParser {

    private Statement<?> runnable;
    private Statement<?> variables;

    @Override
    public boolean matches() {
        assert parent instanceof DoBlockParser : "How did we get here? " + parent.getClass();
        int start = 0;
        do {
            final int space = input.indexOf(' ', start);
            final String before, after;
            if (space < 0) {
                before = input;
                after = null;
            } else {
                before = input.substring(0, space).trim();
                after = input.substring(space + 1).trim();
            }
            start = space + 1;
            ((DoBlockParser) parent).flagDirty();
            this.runnable = parent.parse(before);
            if (!(runnable instanceof VariableStatement variable)) continue;
            this.runnable = new LocalKeywordStatement(variable.name());
            this.checkProperty(variable.name());
            if (after == null) return true;
            if (after.isEmpty()) continue;
            this.variables = parent.parse(after);
            if (variables == null) continue;
            return true;
        } while (start < input.length() && start > 2);
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new LocalFunctionStatement(runnable, variables);
    }

    protected void checkProperty(String name) {
        final Statement<?> source = ((DoBlockParser) parent).getSource();
        final Class<?> type = source.returnType();
        if (type == Object.class) return;
        if (type == Void.class) {
            this.parent.warn("The object '" + source.stringify() + "' may be null.");
            return;
        }
        final Kind<?> kind = Kind.asKind(type);
        if (!kind.hasProperty(name)) parent.warn("The object '" + source.stringify()
            + "' (inferred type " + kind + ") may not have a function '" + name + "'.");
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.runnable = null;
        this.variables = null;
    }

}
