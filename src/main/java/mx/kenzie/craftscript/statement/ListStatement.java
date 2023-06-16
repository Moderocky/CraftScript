package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public record ListStatement(Statement<?>... statements) implements Statement<List<?>> {

    @Override
    public List<?> execute(Context context) throws ScriptError {
        final List<Object> list = new ArrayList<>();
        for (final Statement<?> statement : statements) list.add(statement.execute(context));
        return list;
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
        stream.println('[');
        boolean first = true;
        for (final Statement<?> statement : statements) {
            if (first) first = false;
            else stream.print(", ");
            statement.stringify(stream);
        }
        stream.print(']');
    }

}
