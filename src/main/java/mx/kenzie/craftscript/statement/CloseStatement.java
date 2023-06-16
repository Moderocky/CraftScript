package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;

public record CloseStatement() implements Statement<Void> {

    @Override
    public Void execute(Context context) throws ScriptError {
        throw new ScriptError("This should not be used here.");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("}");
    }

}
