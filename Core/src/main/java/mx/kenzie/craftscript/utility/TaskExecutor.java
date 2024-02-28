package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.Context;

import java.util.function.Supplier;

@FunctionalInterface
public interface TaskExecutor {

    Object execute(Context<?> context, Supplier<Object> task);

}
