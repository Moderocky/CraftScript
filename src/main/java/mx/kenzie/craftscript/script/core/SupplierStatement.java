package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.util.function.Supplier;

public record SupplierStatement(Supplier<Object> supplier) implements NativeStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return supplier.get();
    }

}
