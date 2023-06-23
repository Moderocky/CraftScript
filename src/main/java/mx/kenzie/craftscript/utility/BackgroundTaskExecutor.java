package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.Context;

@FunctionalInterface
public interface BackgroundTaskExecutor {

    void execute(Executable<?> executable, Context context);

}
