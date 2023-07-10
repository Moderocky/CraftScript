package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.statement.Statement;

public abstract class CompiledScript implements AbstractScript {

    protected final String name;

    public CompiledScript(String name) {this.name = name;}

    @Override
    public abstract Object execute(Context context) throws ScriptError;

    protected void prepare(Context context) {
        context.data().script = this;
    }

    protected void line(Context context, int line) {
        context.data().line = new LineStatement(null, line); // todo is it okay if this is null ?
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Statement<?>[] statements() {
        return new Statement[]{this};
    }

}
