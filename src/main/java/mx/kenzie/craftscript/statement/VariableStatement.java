package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;

public record VariableStatement(String name) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return context.variables().get(name);
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
