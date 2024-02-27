package mx.kenzie.craftscript.environment;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public record ScriptTask(AtomicReference<Object> result, Supplier<Object> supplier, Semaphore lock) {

    public ScriptTask(Supplier<Object> supplier) {
        this(new AtomicReference<>(), supplier, new Semaphore(0));
    }

}
