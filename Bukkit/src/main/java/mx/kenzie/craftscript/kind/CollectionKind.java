package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;
import mx.kenzie.craftscript.script.core.InternalStatement;

import java.util.*;

public class CollectionKind<Type extends Collection<?>> extends Kind<Type> {

    public static final CollectionKind<Collection<?>> COLLECTION = new CollectionKind<>(Collection.class, null);

    @SuppressWarnings("unchecked")
    private CollectionKind(Object object, Void unused) {
        this((Class<Type>) object);
    }

    protected CollectionKind(Class<Type> type) {
        super(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getProperty(Type collection, String property) {
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
            case "as_set" -> CheckedFunction.of().runs(() -> new HashSet<>(thing));
            case "as_list" -> CheckedFunction.of().runs(() -> new ArrayList<>(thing));
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[] {"type", "size", "is_empty", "clone", "contains", "remove", "add", "retain", "clear", "as_set", "as_list"};
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Theirs> Type convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Collection<?> collection) return (Type) collection;
        if (object instanceof Map<?, ?> map) return (Type) new ArrayList<>(map.values());
        if (object instanceof Object[] objects) return (Type) new ArrayList<>(Arrays.asList(objects));
        return super.convert(object, kind);
    }

    @Override
    public String toString(Type collection) {
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

}
