package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record ScriptStatement(String name) implements Statement<AbstractScript> {

    @Override
    public AbstractScript execute(Context context) throws ScriptError {
        final AbstractScript script = context.manager().getScript(name);
        if (script == null) throw new ScriptError("Script '" + name + "' is not loaded.");
        return script;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("name=");
        stream.print(name);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(name);
    }

}
