package mx.kenzie.craftscript.script;

public class ScriptRuntimeError extends ScriptError {

    private final Context<?> context;

    public ScriptRuntimeError(String message) {
        super(message);
        this.context = Context.getLocalContext();
    }

    public ScriptRuntimeError(String message, Throwable cause) {
        super(message, cause);
        this.context = Context.getLocalContext();
    }

    public Context<?> getContext() {
        return context;
    }

    public boolean hasContext() {
        return context != null;
    }

}
