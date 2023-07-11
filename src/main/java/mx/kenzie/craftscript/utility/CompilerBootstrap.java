package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;

public interface CompilerBootstrap {

    static Object setVariable(Context context, String name, Object value) {
        context.variables().put(name, value);
        return value;
    }

    static Object getVariable(Context context, String name) {
        return context.variables().get(name);
    }

}
