package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.statement.Statement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;

/**
 * Represents a script or something pretending to be one, e.g. an {@link AnonymousScript}.
 */
public interface AbstractScript extends Statement<Object> {

    String name();

    Statement<?>[] statements();

    @Override
    default Object execute(Context context) throws ScriptError {
        context.data().script = this;
        final Statement<?>[] statements = this.statements();
        for (int i = 0; i < statements.length; i++) {
            final Statement<?> statement = statements[i];
            if (i == statements.length - 1) return statement.execute(context);
            else statement.execute(context);
        }
        return null;
    }

    @Override
    default void debug(PrintStream stream) {
        stream.println("Script '" + this.name() + "':");
        for (final Statement<?> statement : this.statements()) {
            stream.print('\t');
            statement.debug(stream);
            stream.println();
        }
    }

    @Override
    default void stringify(PrintStream stream) {
        for (final Statement<?> statement : this.statements()) {
            statement.stringify(stream);
            stream.println();
        }
    }

    @Override
    default Component prettyPrint(ColorProfile profile) {
        final TextComponent.Builder builder = Component.text();
        for (final Statement<?> statement : statements()) {
            builder.append(statement.prettyPrint(profile));
        }
        return builder.build()
            .hoverEvent(Component.text("A runnable script object.", profile.light()));
    }

}
