package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.variable.LibraryObject;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;

public record LibraryStatement(LibraryObject value) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return new Wrapper<>(value, LibraryObject.LIBRARY);
    }

    public void stringify(PrintStream stream) {
        stream.print("<native code>");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print("[]");
    }

}
