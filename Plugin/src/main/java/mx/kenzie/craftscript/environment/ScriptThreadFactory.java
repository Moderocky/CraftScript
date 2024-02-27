package mx.kenzie.craftscript.environment;

import mx.kenzie.craftscript.script.ScriptThread;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class ScriptThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new ScriptThread(r);
    }

}
