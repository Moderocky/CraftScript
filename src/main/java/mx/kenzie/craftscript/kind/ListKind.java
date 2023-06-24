package mx.kenzie.craftscript.kind;

import java.util.Collection;
import java.util.List;

public class ListKind extends CollectionKind {

    public ListKind() {
        super((Class<Collection>) (Class) List.class);
    }

    @Override
    public Object setProperty(Collection collection, String property, Object value) {
        if (!(collection instanceof List thing)) return null;
        return switch (property) {
            case "index_of" -> thing.indexOf(value);
            default -> super.setProperty(collection, property, value);
        };
    }

}
