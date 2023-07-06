package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static mx.kenzie.craftscript.kind.Kinds.ANY;
import static mx.kenzie.craftscript.kind.Kinds.INTEGER;

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
            case "clone" -> new ArrayList<>(thing);
            case "index_of" -> CheckedFunction.of(ANY).runs(thing::indexOf);
            case "last_index_of" -> CheckedFunction.of(ANY).runs(thing::lastIndexOf);
            case "get" -> CheckedFunction.of(INTEGER).notNull().runs(thing::get);
            case "set" -> CheckedFunction.of(INTEGER, ANY).notNull().runs(thing::set);
            case "slice" -> CheckedFunction.of(INTEGER, INTEGER).notNull().runs(thing::subList);
            default -> super.getProperty(collection, property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"clone", "index_of", "last_index_of", "get", "set", "slice", "type", "size", "is_empty", "clone", "contains", "remove", "add", "retain", "clear", "as_set", "as_list"};
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
