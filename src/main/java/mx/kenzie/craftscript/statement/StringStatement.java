package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Bridge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;

public record StringStatement(String value, Object... parts) implements TextStatement {

    public static String execute(Context context, String value, Object... parts) {
        return Bridge.interpolate(context, parts);
    }

    @Override
    public String execute(Context context) throws ScriptError {
        if (parts.length > 0) return Bridge.interpolate(context, parts);
        else return value;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("value=");
        stream.print(value);
        CommandStatement.debugInterpolations(stream, parts);
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
        final Component component = Component.textOfChildren(Component.text("Text.", profile.light()),
            Component.newline(), this.printReturnType(profile));
        if (parts.length > 0) {
            final TextComponent.Builder builder = Component.text();
            builder.append(Component.text('"', profile.pop()));
            for (final Object object : parts) {
                if (object instanceof String string) builder.append(Component.text(string, profile.light()));
                else if (object instanceof Statement<?> statement) builder.append(statement.prettyPrint(profile));
            }
            builder.append(Component.text('"', profile.pop()));
            return builder.build().hoverEvent(component);
        }
        return Component.textOfChildren(Component.text('"', profile.pop()), Component.text(value, profile.light()),
            Component.text('"', profile.pop())).hoverEvent(component);
    }

}
