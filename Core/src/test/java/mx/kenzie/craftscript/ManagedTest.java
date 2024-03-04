package mx.kenzie.craftscript;

import mx.kenzie.craftscript.script.SimpleScriptLoader;
import mx.kenzie.craftscript.script.SystemScriptManager;
import mx.kenzie.craftscript.variable.Wrapper;

public abstract class ManagedTest {

    protected static SystemScriptManager manager = new SystemScriptManager(SimpleScriptLoader.BASIC);

    protected Object run(String source) {
        return Wrapper.unwrap(manager.runScript(manager.parseScript(source), System.out));
    }

    protected Object debug(String source) {
        manager.parseScript(source).debug(System.out);
        return Wrapper.unwrap(manager.runScript(manager.parseScript(source), System.out));
    }

}
