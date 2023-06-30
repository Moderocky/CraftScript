package mx.kenzie.craftscript.kind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListKind extends CollectionKind {

    public static final ListKind LIST = new ListKind();

    public ListKind() {
        super(List.class);
    }

    @Override
    public Object setProperty(Collection collection, String property, Object value) {
        if (!(collection instanceof List thing)) return super.setProperty(collection, property, value);
        return switch (property) {
            case "index_of" -> thing.indexOf(value);
            default -> super.setProperty(collection, property, value);
        };
    }

    @Override
    public <Theirs> Collection convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Collection<?> collection) return new ArrayList<>(collection);
        return super.convert(object, kind);
    }

}
