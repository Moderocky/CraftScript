package mx.kenzie.craftscript.kind;

import java.util.Objects;

public class NullKind extends Kind<Void> {

    public static final NullKind NULL = new NullKind();

    public NullKind() {
        super(Void.class);
    }

    @Override
    public Object getProperty(Void thing, String property) {
        if (Objects.equals(property, "type")) return this;
        return null;
    }

    @Override
    public <Theirs> Void convert(Theirs object, Kind<? super Theirs> kind) {
        return null;
    }

    @Override
    public String toString(Void unused) {
        return "null";
    }

    @Override
    public String toStringTry(Object object) {
        return "null";
    }

}
