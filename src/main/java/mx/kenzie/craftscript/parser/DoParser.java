package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.DoBlockParser;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptParser;
import mx.kenzie.craftscript.statement.DoStatement;
import mx.kenzie.craftscript.statement.GetterStatement;
import mx.kenzie.craftscript.statement.KindStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.VariableHelper;

public class DoParser extends BasicParser {

    private Statement<?> object;
    private Statement<?> block;

    @Override
    public boolean matches() {
        if (!input.startsWith("do ")) return false;
        int start = 3, original = start;
        do {
            final int space = input.indexOf(' ', start);
            if (space < 0) return false;
            if (start >= input.length()) return false;
            final String before, after;
            before = input.substring(original, space).trim();
            after = input.substring(space + 1).trim();
            start = space + 1;
            this.object = parent.parse(before);
            if (object == null) continue;
            this.setUp(after);
            if (block == null) continue;
            return true;
        } while (start < input.length());
        return false;
    }

    protected void setUp(String after) {
        final VariableHelper helper = VariableHelper.instance();
        final VariableHelper child = helper.clone();
        try {
            final Kind<?> kind = KindStatement.lookForKind(object);
            for (final String property : kind.getProperties()) {
                child.assign(property, new GetterStatement(object, property));
            }
            final ScriptParser parser = new DoBlockParser(object, parent);
            this.block = parser.parse(after);
        } finally {
            VariableHelper.local.set(helper);
            child.purge();
        }
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        if (object.returnType() == Void.class)
            this.parent.warn("The result of '" + object.stringify() + "' is likely null.");
        return new DoStatement(object, block);
    }

    @Override
    public void close() throws ScriptError {
        this.object = null;
        this.block = null;
        super.close();
    }

}
