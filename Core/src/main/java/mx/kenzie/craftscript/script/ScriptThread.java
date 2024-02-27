package mx.kenzie.craftscript.script;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptThread extends Thread {

    volatile boolean dead;

    public ScriptThread() {
        super();
    }

    public ScriptThread(Runnable target) {
        super(target);
    }

    public ScriptThread(@Nullable ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public ScriptThread(@NotNull String name) {
        super(name);
    }

    public ScriptThread(@Nullable ThreadGroup group, @NotNull String name) {
        super(group, name);
    }

    public ScriptThread(Runnable target, String name) {
        super(target, name);
    }

    public ScriptThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name) {
        super(group, target, name);
    }

    public ScriptThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    public ScriptThread(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
    }

    static void step() {
        if (!(Thread.currentThread() instanceof ScriptThread thread)) return;
        if (thread.isDead()) throw new ScriptDeath();
    }

    public static ScriptThread currentThread() {
        return (ScriptThread) Thread.currentThread();
    }

    public void terminate() {
        this.dead = true;
        this.interrupt();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public boolean isInterrupted() {
        return super.isInterrupted();
    }

    public boolean isDead() {
        return dead;
    }

    public void resurrect() {
        this.dead = false;
    }

}
