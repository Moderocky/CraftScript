package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public record InternalStatement(BiFunction<Context, Arguments, Object> function) implements NativeStatement<Object> {

    public InternalStatement(Function<Arguments, Object> function) {
        this((context, arguments) -> function.apply(arguments));
    }

    public InternalStatement(Runnable runnable) {
        this(((context, arguments) -> {
            runnable.run();
            return true;
        }));
    }

    static Arguments extractArguments(Context context) {
        final Arguments arguments;
        final Collection<?> parameters = context.variables().getParameters(), converted;
        if (parameters != null) {
            converted = new ArrayList<>(parameters.size());
            for (final Object parameter : parameters) converted.add(Wrapper.unwrap(parameter));
            arguments = Arguments.of(converted.toArray());
        } else arguments = Arguments.of();
        return arguments;
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        final Arguments arguments = extractArguments(context);
        try {
            return function.apply(context, arguments);
        } catch (ClassCastException ex) {
            throw new ScriptError("Incorrect input types were provided.", ex);
        }
    }

}
