package mx.kenzie.craftscript;

public class ScriptError extends Error {


    public ScriptError(String message) {
        super(message);
    }

    public ScriptError(String message, Throwable cause) {
        super(message, cause);
    }

}
