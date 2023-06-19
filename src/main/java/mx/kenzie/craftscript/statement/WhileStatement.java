package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record WhileStatement(Statement<?> check, Statement<?> then) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        boolean run = false;
        while (IfStatement.value(this.check.execute(context))) {
            this.then.execute(context);
            run = true;
        }
        return run;
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
        stream.print("while ");
        this.check.stringify(stream);
        stream.print(' ');
        this.then.stringify(stream);
    }

}
