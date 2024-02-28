package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.ScriptManager;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.LibraryObject;
import mx.kenzie.craftscript.variable.Wrapper;

public class DebugLibrary extends LibraryObject {

    private static final Executable<?> LINE = context -> {
        final ScriptManager<?> manager = context.manager();
        manager.println(context.getSource(), "Current Line:");
        manager.println(context.getSource(), "File '" + context.data().script.name() + "' line " + context.getLine());
        manager.println(context.getSource(), context.data().line.stringify());
        return null;
    };
    private static final Executable<?> SCRIPT = context -> {
        final ScriptManager<?> manager = context.manager();
        manager.println(context.getSource(), "Current Script:");
        manager.println(context.getSource(), "File '" + context.data().script.name() + "'");
        manager.println(context.getSource(), context.data().script.stringify());
        return null;
    };
    private static final Executable<?> VARIABLES = context -> {
        final ScriptManager<?> manager = context.manager();
        manager.println(context.getSource(), "Current Variable Structure:");
        manager.println(context.getSource(), "File '" + context.data().script.name() + "' line " + context.getLine());
        for (final Entry<String, Object> entry : context.variables().entrySet()) {
            manager.println(context.getSource(), entry.getKey() + ": " + Wrapper.of(entry.getValue()).toString());
        }
        return null;
    };

    protected DebugLibrary(String... keys) {
        super(keys);
    }

    public DebugLibrary() {
        super("variables", "line", "script");
    }

    @Override
    public Object get(String key) {
        return switch (key) {
            case "variables" -> VARIABLES;
            case "line" -> LINE;
            case "script" -> SCRIPT;
            default -> null;
        };
    }

}
