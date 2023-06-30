package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.variable.StructObject;
import mx.kenzie.craftscript.variable.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Kind<Type> {

    protected final Class<Type> type;

    public Kind(Class<Type> type) {
        this.type = type;
    }

    public static Kind<?> asKind(Object thing) {
        if (thing instanceof Kind<?> kind) return kind;
        if (thing instanceof Class<?> kind) return new UnknownKind(kind);
        if (thing == null) return new NullKind();
        return null;
    }

    public static Kind<?> of(Object thing) {
        if (thing == null) return new NullKind();
        if (thing instanceof Wrapper<?> wrapper) return wrapper.getKind();
        if (thing instanceof StructObject object) return object.getKind();
        if (thing instanceof Kind<?>) return new KindKind();
        if (thing instanceof Class<?> kind) return new UnknownKind(kind);
        final Context context = Context.getLocalContext();
        if (context != null)
            for (final Kind<?> kind : context.getKinds()) if (kind.getType().isInstance(thing)) return kind;
        return new UnknownKind(thing.getClass());
    }

    public abstract Object getProperty(Type thing, String property);

    public Object setProperty(Type thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> Objects.equals(thing, value);
            default -> null;
        };
    }

    public boolean checkProperty(Type thing, String property, Object value) {
        return Objects.equals(this.getProperty(thing, property), value);
    }

    public String[] getProperties() {
        return new String[0];
    }

    public Type fromString(String string) {
        return null;
    }

    public String toString(Type type) {
        return Objects.toString(type);
    }

    public String toStringTry(Object object) {
        object = Wrapper.unwrap(object);
        if (this.getType().isInstance(object)) return this.toString((Type) object);
        return Objects.toString(type);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull String print(Object object) {
        final Kind<Object> kind = (Kind<Object>) of(object);
        return kind.toString(object);
    }

    public Class<Type> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Class<?> type && type.equals(this.type)) return true;
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
