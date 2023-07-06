package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListKind extends CollectionKind<List<?>> {

    public static final ListKind LIST = new ListKind();

    @SuppressWarnings("unchecked")
    public ListKind() {
        super((Class<List<?>>) (Object) List.class);
    }

    @Override
    public Object getProperty(List<?> collection, String property) {
        final List<Object> thing = (List<Object>) collection;
        return switch (property) {
            case "index_of" -> CheckedFunction.unary().runs((context, object) -> thing.indexOf(object));
            case "get" -> CheckedFunction.of(Kinds.INTEGER).notNull()
                .runs((context, index) -> thing.get(index));
            case "set" -> CheckedFunction.of(Kinds.INTEGER, Kinds.ANY).notNull()
                .runs((context, index, to) -> thing.set(index, to));
            case "slice" -> CheckedFunction.of(Kinds.INTEGER, Kinds.INTEGER).notNull()
                .runs((context, from, to) -> thing.subList(from, to));
            default -> super.getProperty(collection, property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"index_of", "get", "set", "type", "size", "is_empty", "clone", "contains", "remove", "add", "retain", "clear", "slice"};
    }

    @Override
    public <Theirs> List<?> convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof List<?> list) return list;
        if (object instanceof Collection<?> collection) return new ArrayList<>(collection);
        return super.convert(object, kind);
    }

    @Override
    public Kind<?> superKind() {
        return COLLECTION;
    }

}
