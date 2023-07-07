package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.LazyInterpolatingMap;
import mx.kenzie.craftscript.utility.MapFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;
import java.util.List;

public record StringStatement(String value, InterpolationStatement... interpolations) implements Statement<String> {

    @Override
    public String execute(Context context) throws ScriptError {
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
        CommandStatement.debugInterpolations(stream, interpolations);
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
        final Component component = Component.text("Text.", profile.light());
        if (interpolations.length > 0) {
            final List<Object> list = CommandStatement.interpolateForPrinting(value, interpolations);
            final TextComponent.Builder builder = Component.text();
            builder.append(Component.text('"', profile.pop()));
            for (final Object object : list) {
                if (object instanceof String string) builder.append(Component.text(string, profile.light()));
                else if (object instanceof Statement<?> statement) builder.append(statement.prettyPrint(profile));
            }
            builder.append(Component.text('"', profile.pop()));
            return builder.build().hoverEvent(component);
        }
        return Component.textOfChildren(Component.text('"', profile.pop()), Component.text(value, profile.light()),
            Component.text('"', profile.pop())).hoverEvent(component);
    }

    @Override
    public Class<? extends String> returnType() {
        return String.class;
    }

}
