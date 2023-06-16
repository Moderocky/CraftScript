package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;

public record BlockStatement(Statement<?>... statements) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        for (final Statement<?> statement : statements) statement.execute(context);
        return true;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        for (final Statement<?> statement : statements) {
            stream.print('\t');
            statement.debug(stream);
            stream.println();
        }
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.println('{');
        for (final Statement<?> statement : statements) {
            stream.print('\t');
            statement.stringify(stream);
            stream.println();
        }
        stream.print('}');
    }

}
