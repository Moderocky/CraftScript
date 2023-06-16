package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.Context;

import java.util.Objects;

public abstract class Kind<Type> {

    protected final Class<Type> type;

    public Kind(Class<Type> type) {
        this.type = type;
    }

    public static Kind<?> of(Object thing) {
        if (thing == null) return new NullKind();
        if (thing instanceof Kind<?> kind) return kind;
        if (thing instanceof Class<?> kind) return new UnknownKind(kind);
        final Context context = Context.getLocalContext();
        if (context != null) {
            for (final Kind<?> kind : context.getKinds()) if (kind.getType().isInstance(thing)) return kind;
        }
        return new UnknownKind(thing.getClass());
    }

    public abstract Object getProperty(Type thing, String property);

    public Object setProperty(Type thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.of(value));
            case "equals" -> thing.equals(value);
            default -> null;
        };
    }

    public boolean checkProperty(Type thing, String property, Object value) {
        return Objects.equals(this.getProperty(thing, property), value);
    }

    public String[] getProperties() {
        return new String[0];
    }

    public abstract Type fromString(String string);

    public String toString(Type type) {
        return Objects.toString(type);
    }

    public Class<Type> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final Kind<?> kind)) return false;
        return Objects.equals(type, kind.type);
    }

    @Override
    public String toString() {
        return "#" + this.getType().getSimpleName().toLowerCase();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
