package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.util.function.BiFunction;
import java.util.function.Function;

public record MainThreadStatement(BiFunction<Context, Arguments, Object> function) implements NativeStatement<Object> {

    public MainThreadStatement(Function<Arguments, Object> function) {
        this((context, arguments) -> function.apply(arguments));
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        final Arguments arguments = InternalStatement.extractArguments(context);
        return context.manager().executeOnPrimary(context, () -> function.apply(context, arguments));
    }

}
