package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;

public record IfStatement(Statement<?> check, Statement<?> then) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Object result = this.check.execute(context);
        final boolean test;
        if (result instanceof Boolean boo) test = boo;
        else if (result instanceof Number number) test = number.intValue() != 0;
        else test = result != null;
        if (test) then.execute(context);
        return test;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("check=");
        this.check.debug(stream);
        stream.print(", ");
        stream.print("then=");
        this.then.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("if ");
        this.check.stringify(stream);
        stream.print(' ');
        this.then.stringify(stream);
    }

}
