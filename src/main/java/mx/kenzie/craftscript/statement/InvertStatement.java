package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;

public record InvertStatement(Statement<?> check) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Object result = this.check.execute(context);
        return !IfStatement.value(result);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("check=");
        this.check.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("!");
        this.check.stringify(stream);
    }

}
