package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;

public class ExecutableKind extends Kind<Executable> {

    public ExecutableKind() {
        super(Executable.class);
    }

    protected ExecutableKind(Class<Executable> type) {
        super(type);
    }

    @Override
    public Object getProperty(Executable thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "debug" -> thing instanceof Statement<?> statement
                ? statement.stringify() : thing.toString();
            default -> null;
        };
    }

    @Override
    public Object setProperty(Executable thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            default -> null;
        };
    }

    @Override
    public Executable fromString(String string) {
        return null;
    }

    @Override
    public String toString(Executable executable) {
        if (executable instanceof AbstractScript) return executable.toString();
        return "<executable>";
    }

}
