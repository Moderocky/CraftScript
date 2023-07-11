package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.centurion.selector.Selector;
import mx.kenzie.centurion.selector.Universe;
import mx.kenzie.craftscript.parser.SelectorParser;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.LazyInterpolatingMap;
import mx.kenzie.craftscript.utility.MapFormat;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

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

    public static Object execute(Context context, String text, String[] keys, Executable<?>[] values) {
        final String input;
        if (keys.length > 0) {
            final LazyInterpolatingMap map = new LazyInterpolatingMap(context, keys, values);
            input = MapFormat.format(text, map);
        } else input = text;
        final List<?> list = Selector.of(input, SelectorParser.universe).getAll(context.source());
        if (list.size() == 1) return Wrapper.of(list.get(0));
        return list;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("text=");
        stream.print(text);
        CommandStatement.debugInterpolations(stream, interpolations);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(text);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text(text, profile.light())
            .hoverEvent(Component.textOfChildren(Component.text("Finds entities in the world.", profile.light()),
                Component.newline(), this.printReturnType(profile)));
    }

    @Override
    public Class<?> returnType() {
        if (universe == SelectorParser.universe) return CommandSender.class;
        return Object.class;
    }

}
