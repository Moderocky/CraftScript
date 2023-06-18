package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptManager;
import mx.kenzie.craftscript.variable.VariableContainer;

import java.io.PrintStream;

public record ImportStatement(String... names) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final VariableContainer container = context.variables();
        final ScriptManager manager = context.manager();
        for (final String name : names) {
            final Script script = manager.getScript(name + ".script");
            if (script == null) throw new ScriptError("The script '" + name + ".script' is not loaded.");
            final Object result = RunStatement.run(script, context, new VariableContainer());
            container.put(name, result);
        }
        return true;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(String.join(", ", names));
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("import [");
        stream.print(String.join(", ", names));
        stream.print(']');
    }

}
