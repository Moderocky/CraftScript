package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.centurion.selector.Selector;
import mx.kenzie.centurion.selector.Universe;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.LazyInterpolatingMap;
import mx.kenzie.craftscript.utility.MapFormat;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;
import java.util.List;

public record SelectorStatement(String text, Universe<?> universe,
                                InterpolationStatement... interpolations) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        final String input;
        if (interpolations.length > 0) {
            final LazyInterpolatingMap map = new LazyInterpolatingMap(context, interpolations);
            input = MapFormat.format(text, map);
        } else input = text;
        final List<?> list = Selector.of(input, universe).getAll(context.source());
        if (list.size() == 1) return Wrapper.of(list.get(0));
        return list;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("text=");
        stream.print(text);
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
        stream.print(text);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text(text, profile.light());
    }

}
