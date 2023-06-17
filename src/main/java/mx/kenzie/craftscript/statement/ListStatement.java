package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

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
        boolean first = true;
        for (final Statement<?> statement : statements) {
            if (first) first = false;
            else stream.print(", ");
            statement.debug(stream);
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
