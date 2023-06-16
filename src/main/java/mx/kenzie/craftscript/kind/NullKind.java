package mx.kenzie.craftscript.kind;

import java.util.Objects;

public class NullKind extends Kind<Void> {

    public NullKind() {
        super(Void.class);
    }

    @Override
    public Object getProperty(Void thing, String property) {
        if (Objects.equals(property, "type")) return this;
        return null;
    }

    @Override
    public Void fromString(String string) {
        return null;
    }

}