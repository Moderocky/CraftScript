package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.ScriptError;

public class ScriptCompileError extends ScriptError {

    public ScriptCompileError(String message) {
        super(message);
    }

    public ScriptCompileError(String message, Throwable cause) {
        super(message, cause);
    }

}
