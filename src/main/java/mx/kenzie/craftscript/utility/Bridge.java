package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.RequireStatement;

/**
 * A bridge designed for using script bits in Java.
 */
public interface Bridge {

    static Object[] require(String... names) {
        return RequireStatement.require(Context.requireLocalContext(), names);
    }

    static <Type> Type cast(Object object, Class<Type> type) {
        if (type.isInstance(object)) return type.cast(object);
        throw new ScriptError("Unable to turn '" + object + "' into #" + type.getSimpleName() + ".");
    }

    static void exists(Object object) {
        if (object == null) throw new ScriptError("Provided an empty value.");
    }

}
