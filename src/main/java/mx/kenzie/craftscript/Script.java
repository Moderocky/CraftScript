package mx.kenzie.craftscript;

import mx.kenzie.craftscript.statement.Statement;

import java.io.PrintStream;

public record Script(String name, Statement<?>... statements) implements Executable<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        context.data().script = this;
        for (final Statement<?> statement : statements) {
            try {
                statement.execute(context);
            } catch (ScriptError error) {
                context.manager().printError(error, context.source());
                return false;
            } catch (Throwable ex) {
                context.manager().printError(new ScriptError("Unknown error.", ex), context.source());
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public void debug(PrintStream stream) {
        stream.println("Script '" + name + "':");
        for (final Statement<?> statement : statements) {
            stream.print('\t');
            statement.debug(stream);
            stream.println();
        }
    }

    public void stringify(PrintStream stream) {
        for (final Statement<?> statement : statements) {
            statement.stringify(stream);
            stream.println();
        }
    }

}
