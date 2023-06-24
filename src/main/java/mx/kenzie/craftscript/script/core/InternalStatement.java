package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public record InternalStatement(BiFunction<Context, Arguments, Object> function) implements NativeStatement<Object> {

    public InternalStatement(Function<Arguments, Object> function) {
        this((context, arguments) -> function.apply(arguments));
    }

    public InternalStatement(VoidFunction function) {
        this((Function<Arguments, Object>) function);
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        final Arguments arguments = extractArguments(context);
        return function.apply(context, arguments);
    }

    static Arguments extractArguments(Context context) {
        final Arguments arguments;
        final Collection<?> parameters = (Collection<?>) context.variables()
            .getOrDefault("$parameters", new ArrayList<>()), converted;
        if (parameters != null) {
            converted = new ArrayList<>(parameters.size());
            for (final Object parameter : parameters) converted.add(Wrapper.unwrap(parameter));
            arguments = Arguments.of(converted.toArray());
        } else arguments = Arguments.of();
        return arguments;
    }

    @FunctionalInterface
    public interface ArgumentFunction extends BiFunction<Context, Arguments, Object> {

        Object apply(Arguments arguments);

        @Override
        default Object apply(Context context, Arguments arguments) {
            return this.apply(arguments);
        }

    }

    @FunctionalInterface
    public interface VoidFunction extends Consumer<Arguments>, Function<Arguments, Object> {

        @Override
        default Object apply(Arguments arguments) {
            this.accept(arguments);
            return null;
        }

    }

}
