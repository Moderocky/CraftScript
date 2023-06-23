package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record ConstantStatement(Object value) implements NativeStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return value;
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("<native constant>");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(value);
        stream.print(']');
    }

}
