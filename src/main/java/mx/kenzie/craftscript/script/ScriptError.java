package mx.kenzie.craftscript.script;

public class ScriptError extends Error {


    public ScriptError(String message) {
        super(message);
    }

    public ScriptError(String message, Throwable cause) {
        super(message, cause);
    }

}
