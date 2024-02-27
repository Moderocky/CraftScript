package mx.kenzie.craftscript.script;

public class ScriptError extends Error {

    public ScriptError() {
        super();
    }

    public ScriptError(String message) {
        super(message);
    }

    public ScriptError(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptError(Throwable cause) {
        super(cause);
    }

    protected ScriptError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
