package mx.kenzie.craftscript.kind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionKind extends Kind<Collection> {

    public CollectionKind() {
        super(Collection.class);
    }

    protected CollectionKind(Class<Collection> type) {
        super(type);
    }

    @Override
    public Object getProperty(Collection thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "size" -> thing.size();
            case "is_empty" -> thing.isEmpty();
            case "clone" -> new ArrayList<>(thing);
            default -> null;
        };
    }

    @Override
    public Object setProperty(Collection thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "size" -> thing.size() == ((Number) value).intValue();
            case "contains" -> thing.contains(value);
            case "remove" -> thing.remove(value);
            case "add" -> thing.add(value);
            case "add_all" -> thing.addAll((Collection) value);
            case "contains_all" -> thing.containsAll((Collection) value);
            default -> null;
        };
    }

    @Override
    public Collection fromString(String string) {
        return null;
    }

    @Override
    public String toString(Collection collection) {
        final Iterator iterator = collection.iterator();
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            builder.append(Kind.of(object).toStringTry(object));
            if (iterator.hasNext()) builder.append(", ");
        }
        builder.append(']');
        return builder.toString();
    }

}
