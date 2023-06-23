package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.LibraryObject;
import mx.kenzie.craftscript.variable.Wrapper;

public record LibraryStatement(LibraryObject value) implements NativeStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return new Wrapper<>(value, LibraryObject.LIBRARY);
    }

}
