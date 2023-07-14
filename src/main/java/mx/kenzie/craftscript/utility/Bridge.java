package mx.kenzie.craftscript.utility;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.RequireStatement;
import mx.kenzie.craftscript.variable.Wrapper;

/**
 * A bridge designed for using script bits in Java.
 */
public interface Bridge {

    static String interpolate(Context context, Object... objects) {
        if (objects.length == 0) return "";
        if (objects.length == 1) return Wrapper.of(objects[0]).toString();
        final StringBuilder builder = new StringBuilder();
        for (final Object object : objects) {
            if (object instanceof String string) builder.append(string);
            else if (object instanceof Executable<?> executable) builder.append(Wrapper.of(executable.execute(context)).toString());
            else builder.append(Wrapper.of(object).toString());
        }
        return builder.toString();
    }

    static Object[] require(String... names) {
        return RequireStatement.require(Context.requireLocalContext(), names);
    }

    static <Type> Type cast(Object object, Class<Type> type) {
        if (type.isInstance(object)) return type.cast(object);
        throw new ScriptError("Unable to turn '" + object + "' into #" + type.getSimpleName() + ".");
    }

    @SuppressWarnings("unchecked")
    static <Type, Result extends Type> Result cast(Object object, Kind<Type> type) {
        return (Result) type.convert(object);
    }

    static void exists(Object object) {
        if (object == null) throw new ScriptError("Provided an empty value.");
    }

    static Arguments validate(Arguments arguments, Kind<?>... kinds) {
        final int length = Math.min(arguments.size(), kinds.length);
        final Object[] objects = arguments.toArray(new Object[Math.max(arguments.size(), kinds.length)]);
        for (int i = 0; i < length; i++) {
            try {
                objects[i] = kinds[i].convert(arguments.get(i));
            } catch (ScriptError error) {
                throw new ScriptError("Error with argument " + (i + 1) + ":\n" + error.getMessage());
            }
        }
        return Arguments.of(objects);
    }

    static void validate(Arguments arguments, Class<?>... types) {
        if (arguments.size() < types.length)
            throw new ScriptError("Expected " + types.length + " arguments, found " + arguments.size());
        for (int i = 0; i < types.length; i++) {
            final Object object = arguments.get(i);
            if (object == null) continue;
            if (!types[i].isInstance(object))
                throw new ScriptError(
                    "Argument " + (i + 1) + " expected a #" + Kind.asKind(types[i]) + " but got " + Kind.print(object));
        }
    }

    static void notNull(Arguments arguments) {
        int i = 0;
        for (final Object argument : arguments) {
            i++;
            if (argument != null) continue;
            throw new ScriptError("Argument " + i + " expected a value but was null.");
        }
    }

    static Arguments defaultValues(Arguments arguments, Object... values) {
        final Object[] objects = new Object[Math.max(arguments.size(), values.length)];
        for (int i = 0; i < objects.length; i++) {
            if (i < arguments.size()) {
                final Object found = arguments.get(i);
                if (found != null) objects[i] = found;
                else if (i < values.length) objects[i] = values[i];
            } else objects[i] = values[i];
        }
        return Arguments.of(objects);
    }

    @SuppressWarnings("unchecked")
    static <Type> Class<Type> cast(Object type) {
        return (Class<Type>) type;
    }

}
