package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.InternalStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListKind extends CollectionKind {

    public static final ListKind LIST = new ListKind();

    @SuppressWarnings("unchecked")
    public ListKind() {
        super((Class<List<?>>) (Object) List.class);
    }

    @Override
    public Object getProperty(Collection<?> collection, String property) {
        if (!(collection instanceof List<?> thing)) return super.getProperty(collection, property);
        return switch (property) {
            case "index_of" -> new InternalStatement(arguments -> thing.indexOf(arguments.get(0)));
            case "get" -> new InternalStatement(arguments -> thing.get(arguments.<Number>get(0).intValue()));
            case "set" ->
                new InternalStatement(arguments -> thing.set(arguments.<Number>get(0).intValue(), arguments.get(1)));
            default -> super.getProperty(collection, property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"index_of", "get", "set", "type", "size", "is_empty", "clone", "contains", "remove", "add", "retain"};
    }

    @Override
    public <Theirs> Collection<?> convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Collection<?> collection) return new ArrayList<>(collection);
        return super.convert(object, kind);
    }

    @Override
    public Kind<?> superKind() {
        return COLLECTION;
    }

}
