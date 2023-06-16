package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Collection;

public record ForStatement(VariableAssignmentStatement assignment, Statement<?> then) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final String name = assignment.name();
        final Statement<?> fetch = assignment.statement();
        final Object values = fetch.execute(context);
        if (values instanceof Collection<?> collection) for (final Object value : collection) {
            context.variables().put(name, value);
            this.then.execute(context);
        }
        else if (values instanceof Object[] objects) for (final Object value : objects) {
            context.variables().put(name, value);
            this.then.execute(context);
        }
        else if (values == null) return false;
        else if (values.getClass().isArray()) {
            final int length = Array.getLength(values);
            for (int i = 0; i < length; i++) {
                final Object value = Array.get(values, i);
                context.variables().put(name, value);
                this.then.execute(context);
            }
        } else {
            context.variables().put(name, values);
            this.then.execute(context);
        }
        return true;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("assignment=");
        this.assignment.debug(stream);
        stream.print(", ");
        stream.print("then=");
        this.then.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("for ");
        this.assignment.stringify(stream);
        stream.print(' ');
        this.then.stringify(stream);
    }

}
