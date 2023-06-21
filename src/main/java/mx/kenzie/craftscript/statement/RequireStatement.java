package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.VariableContainer;

import java.io.PrintStream;
import java.util.Collection;

public record RequireStatement(String... names) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        return require(context, names).length == names.length;
    }

    public static Object[] require(Context context, String... names) {
        final VariableContainer container = context.variables();
        final Object[] required = new Object[names.length];
        if (container.get("$parameters") instanceof Collection<?> collection) {
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
        for (int i = 0; i < names.length; i++) {
            final String name = names[i];
            if (!container.containsKey(name)) throw new ScriptError("The variable '" + name + "' is not present.");
            required[i] = container.get(name);
        }
        return required;
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
