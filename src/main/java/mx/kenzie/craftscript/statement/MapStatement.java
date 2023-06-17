package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.VariableFinder;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public record MapStatement(Statement<?>... statements) implements Statement<Map<String, Object>> {

    @Override
    public Map<String, Object> execute(Context context) throws ScriptError {
        final VariableContainer container = new VariableFinder(context.variables());
        final Context sub = new Context(context.source(), context.manager(), container, context.data());
        for (final Statement<?> statement : statements) statement.execute(sub);
        return new HashMap<>(container);
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
