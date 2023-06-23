package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public record ListStatement(Statement<?>... statements) implements Statement<List<?>> {

    @Override
    public List<?> execute(Context context) throws ScriptError {
        final List<Object> list = new ArrayList<>();
        for (final Statement<?> statement : statements) list.add(statement.execute(context));
        return list;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        boolean first = true;
        for (final Statement<?> statement : statements) {
            if (first) first = false;
            else stream.print(", ");
            statement.debug(stream);
        }
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.println('[');
        boolean first = true;
        for (final Statement<?> statement : statements) {
            if (first) first = false;
            else stream.print(", ");
            statement.stringify(stream);
        }
        stream.print(']');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text('[', profile.pop()));
        for (int i = 0; i < statements.length; i++) {
            final Statement<?> statement = statements[i];
            if (i > 0) builder.append(Component.text(", ", profile.pop()));
            builder.append(statement.prettyPrint(profile));
        }
        builder.append(Component.text(']', profile.pop()));
        return builder.build().hoverEvent(Component.text("A list of objects.", profile.light()));
    }

}
