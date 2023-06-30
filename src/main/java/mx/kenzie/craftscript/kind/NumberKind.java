package mx.kenzie.craftscript.kind;

import java.util.Objects;

public class NumberKind extends Kind<Number> {

    public static final NumberKind NUMBER = new NumberKind();

    public NumberKind() {
        super(Number.class);
    }

    @SuppressWarnings("unchecked")
    protected NumberKind(Class<? extends Number> type) {
        super((Class<Number>) type);
    }

    @Override
    public Object getProperty(Number thing, String property) {
        if (Objects.equals(property, "type")) return this;
        return null;
    }

    @Override
    public <Theirs> Number convert(Theirs object, Kind<? super Theirs> kind) {
        if (object == null) return 0;
        else if (object instanceof String string) return Double.valueOf(string);
        else return super.convert(object, kind).intValue();
    }

}
