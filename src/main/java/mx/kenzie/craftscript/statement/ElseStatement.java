package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record ElseStatement() implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        throw new ScriptError("Trailing 'else' statement.");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("} else {");
    }

}
