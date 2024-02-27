package mx.kenzie.craftscript.kind;

import java.util.*;

public class SetKind extends CollectionKind<Set<?>> {

    public static final SetKind SET = new SetKind();

    @SuppressWarnings("unchecked")
    public SetKind() {
        super((Class<Set<?>>) (Object) Set.class);
    }

    @Override
    public Object getProperty(Set<?> thing, String property) {
        return switch (property) {
            case "clone" -> new HashSet<>(thing);
            default -> super.getProperty(thing, property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[] {"clone", "type", "size", "is_empty", "clone", "contains", "remove", "add", "retain", "clear", "as_set", "as_list"};
    }

    @Override
    public <Theirs> Set<?> convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Set<?> set) return set;
        if (object instanceof Collection<?> collection) return new HashSet<>(collection);
        if (object instanceof Map<?, ?> map) return new HashSet<>(map.values());
        if (object instanceof Object[] objects) return new HashSet<>(Arrays.asList(objects));
        return super.convert(object, kind);
    }

    @Override
    public Kind<?> superKind() {
        return COLLECTION;
    }

}
