package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.VariableContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;
import java.util.Collection;

public record RequireStatement(String... names) implements Statement<Boolean> {

    public static Object[] require(Context context, String... names) {
        final VariableContainer container = context.variables();
        final Object[] required = new Object[names.length];
        final Collection<?> collection = container.getParameters();
        final Object[] objects = collection.toArray();
        for (int i = 0; i < names.length; i++) {
            final String name = names[i];
            if (container.containsKey(name)) required[i] = container.get(name);
            else if (i < objects.length) {
                final Object alt = objects[i];
                required[i] = alt;
                container.put(names[i], alt);
            } else throw new ScriptError("The variable '" + name + "' is not present.");
        }
        return required;
    }

    static void printNames(TextComponent.Builder builder, String[] names, ColorProfile profile) {
        builder.append(Component.text('[', profile.pop()));
        for (int i = 0; i < names.length; i++) {
            if (i > 0) builder.append(Component.text(", ", profile.pop()));
            builder.append(Component.text(names[i], profile.highlight()));
        }
        builder.append(Component.text(']', profile.pop()));
    }

    @Override
    public Boolean execute(Context context) throws ScriptError {
        return require(context, names).length == names.length;
    }

    public static Boolean execute(Context context, String[] names) {
        return require(context, names).length == names.length;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(String.join(", ", names));
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("require [");
        stream.print(String.join(", ", names));
        stream.print(']');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text("require ", profile.dark()));
        printNames(builder, names, profile);
        return builder.build()
            .hoverEvent(
                Component.textOfChildren(Component.text("Requires these variables are present.", profile.light()),
                    Component.newline(), this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Boolean> returnType() {
        return Boolean.class;
    }

}
