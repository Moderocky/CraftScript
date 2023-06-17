package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;

public record VariableAssignmentStatement(String name, Statement<?> statement) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        final Object result = this.statement.execute(context);
        context.variables().put(name, Wrapper.of(result));
        return result;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("name=");
        stream.print(name);
        stream.print(", ");
        stream.print("value=");
        this.statement.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(name);
        stream.print("=");
        this.statement.stringify(stream);
    }

}
