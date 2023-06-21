package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public record InternalStatement(Function<Arguments, Object> function) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        final Arguments arguments;
        final Collection<?> parameters = (Collection<?>) context.variables()
            .getOrDefault("$parameters", new ArrayList<>());
        if (parameters != null) arguments = Arguments.of(parameters.toArray());
        else arguments = Arguments.of();
        return function.apply(arguments);
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("<native code>");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print("[]");
    }

}
