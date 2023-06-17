package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record FunctionStatement(Statement<?> executable) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return executable;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        stream.print("executable=");
        this.executable.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("function ");
        this.executable.debug(stream);
    }

}
