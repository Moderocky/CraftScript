package mx.kenzie.craftscript.script;

public class ScriptError extends Error {

    private final Context context;

    public ScriptError(String message) {
        super(message);
        this.context = Context.getLocalContext();
    }

    public ScriptError(String message, Throwable cause) {
        super(message, cause);
        this.context = Context.getLocalContext();
    }

    public Context getContext() {
        return context;
    }

    public boolean hasContext() {
        return context != null;
    }

}
