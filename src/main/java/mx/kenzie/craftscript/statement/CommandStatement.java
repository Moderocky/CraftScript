package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.LazyInterpolatingMap;
import mx.kenzie.craftscript.utility.MapFormat;

import java.io.PrintStream;

public record CommandStatement(String input, InterpolationStatement... interpolations) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final String command;
        if (interpolations.length > 0) {
            final LazyInterpolatingMap map = new LazyInterpolatingMap(context, interpolations);
            command = MapFormat.format(input, map);
        } else command = input;
        return context.manager().runCommand(context, command);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("input=");
        stream.print(input);
        if (interpolations.length > 0) {
            stream.print(", ");
            stream.print("interpolations=[");
            for (final InterpolationStatement interpolation : interpolations) {
                interpolation.debug(stream);
            }
            stream.print(']');
        }
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print('/');
        stream.print(input);
    }

}
