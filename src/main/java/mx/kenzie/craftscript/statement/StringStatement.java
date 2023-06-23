package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.LazyInterpolatingMap;
import mx.kenzie.craftscript.utility.MapFormat;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record StringStatement(String value, InterpolationStatement... interpolations) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        if (interpolations.length > 0) {
            final LazyInterpolatingMap map = new LazyInterpolatingMap(context, interpolations);
            return MapFormat.format(value, map);
        } else return value;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("value=");
        stream.print(value);
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
        stream.print('"');
        stream.print(value);
        stream.print('"');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text('"', profile.pop()),
            Component.text(value, profile.light()),
            Component.text('"', profile.pop())
        );
    }

}
