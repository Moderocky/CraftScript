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
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "size" -> thing.size() == ((Number) value).intValue();
            case "index_of" -> thing.indexOf(value);
            case "contains" -> thing.contains(value);
            case "remove" -> thing.remove(value);
            case "add" -> thing.add(value);
            case "add_all" -> thing.addAll((Collection) value);
            case "contains_all" -> thing.containsAll((Collection) value);
            default -> null;
        };
    }

}
