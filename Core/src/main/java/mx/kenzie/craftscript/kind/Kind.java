package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.StructObject;
import mx.kenzie.craftscript.variable.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public abstract class Kind<Type> {

    protected final Class<Type> type;

    public Kind(Class<Type> type) {
        this.type = type;
    }

    public static Kind<?> asKind(Object thing) {
        if (thing instanceof Kind<?> kind) return kind;
        if (thing instanceof Class<?> kind) {
            final Context<?> context = Context.getLocalContext();
            if (context != null) for (final Kind<?> found : context.getKinds()) {
                if (found.type.isAssignableFrom(kind)) return found;
            }
            else for (final Kind<?> found : Kinds.kinds) {
                if (found.getType().isInstance(thing)) return found;
            }
            return new UnknownKind(kind);
        }
        if (thing == null) return NullKind.NULL;
        return UnknownKind.ANY;
    }

    public static Kind<?> of(Object thing) {
        if (thing == null) return NullKind.NULL;
        if (thing instanceof Wrapper<?> wrapper) return wrapper.getKind();
        if (thing instanceof StructObject object) return object.getKind();
        if (thing instanceof Kind<?>) return new KindKind();
        if (thing instanceof Class<?> kind) return new UnknownKind(kind);
        final Context context = Context.getLocalContext();
        final Iterator<Kind<?>> iterator = kinds(context);
        while (iterator.hasNext()) {
            final Kind<?> kind = iterator.next();
            if (kind.getType().isInstance(thing)) return kind;
        }
        return new UnknownKind(thing.getClass());
    }

    private static Iterator<Kind<?>> kinds(Context context) {
        if (context != null) return context.kinds();
        return Kinds.kinds.iterator();
    }

    @SuppressWarnings("unchecked")
    public static @NotNull String print(Object object) {
        final Kind<Object> kind = (Kind<Object>) of(object);
        return kind.toString(object);
    }

    public Object getProperty(Type thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            default -> null;
        };
    }

    public Object setProperty(Type thing, String property, Object value) {
        return null;
    }

    public boolean checkProperty(Type thing, String property, Object value) {
        return Objects.equals(this.getProperty(thing, property), value);
    }

    public String[] getProperties() {
        return new String[] {"type"};
    }

    public Class<?> getTypeHint(String property) {
        return Object.class;
    }

    public final Type convert(Object object) {
        final Wrapper<Object> wrapper = Wrapper.of(object);
        final Kind<Object> theirs = wrapper.getKind();
        final Type result;
        if (type.isInstance(object)) result = type.cast(object);
        else result = this.convert(wrapper.getValue(), theirs);
        return result;
    }

    public <Theirs> Type convert(Theirs object, Kind<? super Theirs> kind) {
        if (object == null) return null;
        throw new ScriptError("Unable to convert a " + kind + " to a " + this);
    }

    public String toString(Type type) {
        return Objects.toString(type);
    }

    public String toStringTry(Object object) {
        object = Wrapper.unwrap(object);
        if (this.getType().isInstance(object)) return this.toString(this.getType().cast(object));
        return Objects.toString(type);
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
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "#" + this.getType().getSimpleName().toLowerCase();
    }

    public Kind<?> superKind() {
        return this;
    }

    public void setupDoBlock(Context<?> context) {
        assert context != null;
    }

    public boolean hasProperty(String name) {
        return Set.of(this.getProperties()).contains(name);
    }

}
