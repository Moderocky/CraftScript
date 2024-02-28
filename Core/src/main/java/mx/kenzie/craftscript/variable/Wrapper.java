package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.kind.Kind;

import java.util.Objects;

public class Wrapper<Type> {

    protected final Kind<Type> kind;
    protected final Type thing;

    @SuppressWarnings("unchecked")
    public Wrapper(Type thing) {
        this(thing, (Kind<Type>) Kind.of(thing));
    }

    public <Thing extends Type> Wrapper(Thing thing, Kind<Type> kind) {
        this.thing = thing;
        this.kind = kind;
    }

    @SuppressWarnings("unchecked")
    public static <Type> Wrapper<Type> of(Object thing) {
        if (thing instanceof Wrapper<?> wrapper) return (Wrapper<Type>) wrapper;
        return (Wrapper<Type>) new Wrapper<>(thing);
    }

    @SuppressWarnings("unchecked")
    public static <Type> Type unwrap(Object object) {
        if (object instanceof Wrapper<?> wrapper) return (Type) wrapper.getValue();
        return (Type) object;
    }

    public Kind<Type> getKind() {
        return kind;
    }

    public Type getValue() {
        return thing;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof final Wrapper<?> wrapper)) return false;
        return Objects.equals(thing, wrapper.thing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, thing);
    }

    @Override
    public String toString() {
        return kind.toStringTry(thing);
    }

}
