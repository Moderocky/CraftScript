package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Bridge;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.Interpolator;
import mx.kenzie.craftscript.variable.LibraryObject;

import java.util.Objects;

public class ReflectionLibrary extends LibraryObject {

    private static final Executable<?> LINE = new InternalStatement(arguments -> {
        final Context context = Context.requireLocalContext().getParentContext();
        if (arguments.isEmpty()) return context.data().line;
        else if (arguments.get(0) instanceof Number number) {
            for (Statement<?> statement : context.data().script.statements()) {
                if (statement instanceof LineStatement line && line.line() == number.intValue()) return statement;
            }
        } else if (arguments.get(0) instanceof AbstractScript script && arguments.get(1) instanceof Number number) {
            for (Statement<?> statement : script.statements()) {
                if (statement instanceof LineStatement line && line.line() == number.intValue()) return statement;
            }
        }
        return null;
    });
    private static final Executable<?> SCRIPT = context -> context.getParentContext().data().script;
    private static final Executable<?> ROOT = context -> context.getRootContext().data().script;
    private static final Executable<?> VARIABLES = context -> context.getParentContext().variables();
    private static final Executable<?> INTERPOLATE = new InternalStatement((ours, arguments) -> {
        final Context context = ours.getParentContext();
        final String text = Objects.toString(arguments.get(0));
        final Object[] parts = new Interpolator(text, context.manager().getParser()).interpolations();
        if (parts.length == 0 || (parts.length == 1 && parts[0] instanceof String)) return text;
        else return Bridge.interpolate(context, parts);
    });

    public ReflectionLibrary() {
        super("variables", "line", "script", "root", "interpolate");
    }

    @Override
    public Object get(String key) {
        return switch (key) {
            case "variables" -> VARIABLES;
            case "line" -> LINE;
            case "script" -> SCRIPT;
            case "root" -> ROOT;
            case "interpolate" -> INTERPOLATE;
            default -> null;
        };
    }

}
