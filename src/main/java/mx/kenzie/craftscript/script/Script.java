package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;

import java.io.PrintStream;
import java.util.Objects;

public record Script(String name, Statement<?>... statements) implements Executable<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        context.data().script = this;
        Object result = null;
        for (final Statement<?> statement : statements) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final Script script)) return false;
        return Objects.equals(name, script.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
