package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.VariableContainer;

import java.io.PrintStream;
import java.util.Collection;

public record RequireStatement(String... names) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final VariableContainer container = context.variables();
        list:
        if (container.containsKey("$parameters")) {
            final Object parameters = container.get("$parameters");
            if (parameters == null) break list;
            final Object[] objects;
            if (parameters instanceof Collection<?> collection) objects = collection.toArray();
            else if (parameters instanceof Object[] array) objects = array;
            else objects = new Object[]{parameters};
            for (int i = 0; i < names.length; i++) {
                final String name = names[i];
                if (i < objects.length) {
                    final Object alt = objects[i];
                    container.put(names[i], alt);
                } else if (!container.containsKey(name))
                    throw new ScriptError("The variable '" + name + "' is not present.");
            }
            return true;
        }
        for (final String name : names)
            if (!container.containsKey(name)) throw new ScriptError("The variable '" + name + "' is not present.");
        return true;
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

}
