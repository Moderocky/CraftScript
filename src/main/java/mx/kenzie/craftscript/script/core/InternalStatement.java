package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public record InternalStatement(Function<Arguments, Object> function) implements NativeStatement<Object> {

    public InternalStatement(VoidFunction function) {
        this((Function<Arguments, Object>) function);
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        final Arguments arguments;
        final Collection<?> parameters = (Collection<?>) context.variables()
            .getOrDefault("$parameters", new ArrayList<>());
        if (parameters != null) arguments = Arguments.of(parameters.toArray());
        else arguments = Arguments.of();
        return function.apply(arguments);
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
