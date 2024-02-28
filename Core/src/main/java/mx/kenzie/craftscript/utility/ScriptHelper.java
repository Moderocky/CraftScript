package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.ScriptManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public interface ScriptHelper {

    Map<ScriptManager<?>, ScriptHelper> map = Collections.synchronizedMap(new WeakHashMap<>());
    ThreadLocal<ScriptHelper> local = new ThreadLocal<>();
    ScriptHelper EMPTY = new ScriptHelper() {
    };

    static void init(ScriptManager<?> manager) {
        final ScriptHelper helper = new SimpleScriptHelper();
        map.put(manager, helper);
        local.set(helper);
        for (final AbstractScript script : manager.getScripts()) {
            helper.setReturnType(script.name(), script.returnType());
        }
    }

    static void tearDown() {
        local.remove();
    }

    static void tearDown(ScriptManager manager) {
        map.remove(manager);
    }

    static ScriptHelper instance() {
        final ScriptHelper helper = local.get();
        if (helper == null) return EMPTY;
        return helper;
    }

    default Class<?> getReturnType(String script) {
        return Object.class;
    }

    default void setReturnType(String script, Class<?> type) {
    }

}

class SimpleScriptHelper implements ScriptHelper {

    protected final Map<String, Class<?>> map = new HashMap<>();

    @Override
    public Class<?> getReturnType(String script) {
        return map.getOrDefault(script, Object.class);
    }

    @Override
    public void setReturnType(String script, Class<?> type) {
        this.map.put(script, type);
    }

}
