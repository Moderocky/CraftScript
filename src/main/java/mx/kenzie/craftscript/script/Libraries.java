package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.core.InternalStatement;
import mx.kenzie.craftscript.script.core.LibraryStatement;
import mx.kenzie.craftscript.script.core.MathLibrary;

public interface Libraries {

    Script MATH = new Script("math.script", new LibraryStatement(new MathLibrary()));
    Script PARSER = new Script("parser.script", new InternalStatement(arguments -> {
        final Context context = Context.getLocalContext();
        if (context == null) throw new ScriptError("No script parser is available here.");
        return context.manager().parseScript(Kind.of(arguments.get(0)).toString());
    }));

}
