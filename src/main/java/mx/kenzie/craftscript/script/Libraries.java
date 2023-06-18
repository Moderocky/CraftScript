package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.script.core.LibraryStatement;
import mx.kenzie.craftscript.script.core.MathLibrary;

public interface Libraries {

    Script MATH = new Script("math.script", new LibraryStatement(new MathLibrary()));

}
