package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.core.SupplierStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;

import java.util.Objects;

public class StatementKind extends ExecutableKind {

    public static final StatementKind STATEMENT = new StatementKind();

    public StatementKind() {
        super(Statement.class);
    }

    @Override
    public Object getProperty(Executable thing, String property) {
        if (thing == null) return null;
        if (!(thing instanceof Statement<?> statement)) return super.getProperty(thing, property);
        return switch (property) {
            case "type" -> this;
            case "debug" -> statement.stringify();
            default -> super.getProperty(thing, property);
        };
    }

    @Override
    public String[] getProperties() {
        return this.superKind().getProperties();
    }

    @Override
    public String toString(Executable executable) {
        if (executable instanceof AbstractScript) return executable.toString();
        if (executable instanceof SupplierStatement statement) return Objects.toString(statement.supplier().get());
        if (executable == null) return "null";
        final Class<?> type = executable.getClass();
        if (type.isAnonymousClass() || type.isSynthetic() || type.isHidden()) return "<statement>";
        final String name = type.getSimpleName().replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");
        final String converted = name.replaceAll("([a-z])([A-Z])", "$1 $2");
        return "<" + converted.toLowerCase() + ">";
    }

    @Override
    public Kind<?> superKind() {
        return EXECUTABLE;
    }

}
