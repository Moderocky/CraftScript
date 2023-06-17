package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record BlockStatement(Statement<?>... statements) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        Object result = null;
        for (final Statement<?> statement : statements) result = statement.execute(context);
        return result;
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
