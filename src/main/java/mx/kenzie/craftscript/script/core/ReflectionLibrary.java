package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.parser.StringParser;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.LazyInterpolatingMap;
import mx.kenzie.craftscript.utility.MapFormat;
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
        final InterpolationStatement[] statements = StringParser
            .interpolations(text, context.manager().getParser());
        if (statements.length > 0) {
            final LazyInterpolatingMap map = new LazyInterpolatingMap(context, statements);
            return MapFormat.format(text, map);
        } else return text;
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
