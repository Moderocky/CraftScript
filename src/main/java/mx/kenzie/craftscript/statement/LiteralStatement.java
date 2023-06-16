package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;

public record LiteralStatement(Object value) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return value;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("value=");
        stream.print(value);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(value);
    }

}
