package mx.kenzie.craftscript.script;

/**
 * An uncaught, silent error that propagates up a script runner.
 * Its aim is to kill the current script's progress entirely.
 */
public class ScriptDeath extends ScriptError {

    public ScriptDeath() {
        super("Script forcibly terminated.");
    }

}
