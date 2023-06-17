package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record NullStatement() implements Statement<Void> {

    @Override
    public Void execute(Context context) throws ScriptError {
        return null;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("null");
    }

}
