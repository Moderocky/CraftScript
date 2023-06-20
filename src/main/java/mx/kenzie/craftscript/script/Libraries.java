package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.script.core.InternalStatement;
import mx.kenzie.craftscript.script.core.LibraryStatement;
import mx.kenzie.craftscript.script.core.MathLibrary;
import mx.kenzie.craftscript.script.core.SupplierStatement;

public interface Libraries {

    LibraryScript MATH = new LibraryScript("math.script", new LibraryStatement(new MathLibrary()));
    LibraryScript PARSER = new LibraryScript("parser.script",
        new SupplierStatement(() -> new InternalStatement(arguments -> {
            final Context context = Context.getLocalContext();
            if (context == null) throw new ScriptError("No script parser is available here.");
            return context.manager().parseScript((String) arguments.get(0));
        })));
    LibraryScript GLOBAL = new LibraryScript("global.script", new SupplierStatement(() -> {
        final Context context = Context.getLocalContext();
        if (context == null) throw new ScriptError("No script environment is available here.");
        return context.manager().getGlobalVariables();
    }));

}
