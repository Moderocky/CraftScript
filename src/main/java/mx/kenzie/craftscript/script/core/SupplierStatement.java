package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;

import java.io.PrintStream;
import java.util.function.Supplier;

public record SupplierStatement(Supplier<Object> supplier) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return supplier.get();
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("<native code>");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(']');
    }

}
