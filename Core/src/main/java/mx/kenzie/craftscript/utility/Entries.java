package mx.kenzie.craftscript.utility;

import java.util.Map;

public interface Entries extends Map<String, Object> {

    @SuppressWarnings("unchecked")
    default <Type> Type get(String key) {
        return (Type) this.get((Object) key);
    }

    @SuppressWarnings("unchecked")
    default <Type> Type getOrDefault(String key, Type alternative) {
        try {
            return (Type) this.getOrDefault((Object) key, alternative);
        } catch (ClassCastException ex) {
            return alternative;
        }
    }

    default <Type> Type get(String key, Class<Type> type) {
        final Object found = this.get((Object) key);
        if (type.isInstance(found)) return type.cast(found);
        return null;
    }

    default <Type> Type getOrDefault(String key, Class<Type> type, Type alternative) {
        final Object found = this.getOrDefault((Object) key, alternative);
        if (type.isInstance(found)) return type.cast(found);
        return alternative;
    }

}
