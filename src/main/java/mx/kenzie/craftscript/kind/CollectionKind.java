package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.InternalStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class CollectionKind extends Kind<Collection<?>> {

    public static final CollectionKind COLLECTION = new CollectionKind();

    @SuppressWarnings("unchecked")
    public CollectionKind() {
        this((Class<Collection<?>>) (Object) Collection.class);
    }

    @SuppressWarnings("unchecked")
    protected CollectionKind(Class<? extends Collection<?>> type) {
        super((Class<Collection<?>>) type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getProperty(Collection<?> collection, String property) {
        if (collection == null) return null;
        final Collection<Object> thing = (Collection<Object>) collection;
        return switch (property) {
            case "type" -> this;
            case "size" -> thing.size();
            case "is_empty" -> thing.isEmpty();
            case "clone" -> new ArrayList<>(thing);
            case "contains" -> new InternalStatement(thing::containsAll);
            case "remove" -> new InternalStatement(thing::removeAll);
            case "add" -> new InternalStatement(thing::addAll);
            case "retain" -> new InternalStatement(thing::retainAll);
            case "clear" -> new InternalStatement(thing::clear);
            default -> null;
        };
    }

    @Override
    public String toString(Collection<?> collection) {
        final Iterator<?> iterator = collection.iterator();
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

    @Override
    public String[] getProperties() {
        return new String[]{"type", "size", "is_empty", "clone", "contains", "remove", "add", "retain", "clear"};
    }

    @Override
    public <Theirs> Collection<?> convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Collection<?> collection) return new ArrayList<>(collection);
        if (object instanceof Map<?, ?> map) return new ArrayList<>(map.values());
        return super.convert(object, kind);
    }

}
