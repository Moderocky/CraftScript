package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.statement.Statement;

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
        Object result = null;
        for (final Statement<?> statement : this.statements()) {
            try {
                result = statement.execute(context);
            } catch (ScriptError error) {
                context.manager().printError(error, context.source());
                return null;
            } catch (Throwable ex) {
                context.manager().printError(new ScriptError("Unknown error.", ex), context.source());
                return null;
            }
        }
        return result;
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

}
