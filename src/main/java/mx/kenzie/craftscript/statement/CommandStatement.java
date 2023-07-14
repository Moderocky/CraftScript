package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Bridge;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.LazyInterpolatingMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record CommandStatement(String input, Object... parts) implements Statement<Boolean> {

    public static List<Object> interpolateForPrinting(String string, InterpolationStatement... statements) {
        final LazyInterpolatingMap map = new LazyInterpolatingMap(new Context(null, null), statements);
        List<Object> list = List.of(string);
        for (final Map.Entry<String, Executable<?>> entry : map.realEntrySet()) {
            final String key = entry.getKey();
            final List<Object> replacement = new LinkedList<>();
            for (final Object object : list) {
                if (!(object instanceof String text)) {
                    replacement.add(object);
                    continue;
                }
                final int index = text.indexOf(key);
                if (index < 0) {
                    replacement.add(text);
                    continue;
                }
                final String before = text.substring(0, index), after;
                after = text.substring(index + key.length());
                replacement.add(before);
                replacement.add(entry.getValue());
                replacement.add(after);
            }
            if (replacement.size() < 2) continue;
            list = replacement;
        }
        return list;
    }

    static void debugInterpolations(PrintStream stream, Object... parts) {
        stream.print(", parts=[");
        for (final Object part : parts) {
            if (part instanceof String string) stream.print("\"" + string + "\", ");
            else if (part instanceof Statement<?> statement) statement.debug(stream);
        }
        stream.print(']');
    }

    public static Boolean execute(Context context, String input, Object... parts) {
        final String command;
        if (parts.length > 0) command = Bridge.interpolate(context, parts);
        else command = input;
        return context.manager().runCommand(context, command);
    }

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final String command;
        if (parts.length > 0) command = Bridge.interpolate(context, parts);
        else command = input;
        return context.manager().runCommand(context, command);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("input=");
        stream.print(input);
        CommandStatement.debugInterpolations(stream, parts);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print('/');
        stream.print(input);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final List<InterpolationStatement> statements = new ArrayList<>();
        for (final Object part : parts) {
            if (part instanceof InterpolationStatement statement) statements.add(statement);
        }
        if (!statements.isEmpty()) {
            final List<Object> list = CommandStatement.interpolateForPrinting(input,
                statements.toArray(new InterpolationStatement[0]));
            final TextComponent.Builder builder = Component.text();
            builder.append(Component.text('/', profile.pop()));
            for (final Object object : list) {
                if (object instanceof String string) builder.append(Component.text(string, profile.light()));
                else if (object instanceof Statement<?> statement)
                    builder.append(statement.prettyPrint(profile));
            }
            return builder.build().hoverEvent(
                Component.textOfChildren(Component.text("A runnable command.", profile.light()), Component.newline(),
                    this.printReturnType(profile)));
        }
        return Component.textOfChildren(
            Component.text('/', profile.pop()),
            Component.text(input, profile.light())
        ).hoverEvent(
            Component.textOfChildren(Component.text("A runnable command.", profile.light()), Component.newline(),
                this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Boolean> returnType() {
        return Boolean.class;
    }

    @Override
    public boolean knowsReturnType() {
        return true;
    }

}
