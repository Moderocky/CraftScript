package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;

@FunctionalInterface
public interface BackgroundTaskExecutor {

    void execute(AbstractScript source, Executable<?> executable, Context context);

}
