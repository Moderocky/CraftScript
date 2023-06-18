package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.script.core.InternalStatement;
import mx.kenzie.craftscript.script.core.LibraryStatement;
import mx.kenzie.craftscript.script.core.MathLibrary;
import mx.kenzie.craftscript.script.core.SupplierStatement;

public interface Libraries {

    Script MATH = new Script("math.script", new LibraryStatement(new MathLibrary()));
    Script PARSER = new Script("parser.script", new SupplierStatement(() -> new InternalStatement(arguments -> {
        final Context context = Context.getLocalContext();
        if (context == null) throw new ScriptError("No script parser is available here.");
        final AbstractScript script = context.manager().parseScript((String) arguments.get(0));
        return script;
    })));
    Script GLOBAL = new Script("global.script", new SupplierStatement(() -> {
        final Context context = Context.getLocalContext();
        if (context == null) throw new ScriptError("No script environment is available here.");
        return context.manager().getGlobalVariables();
    }));

}
