package mx.kenzie.craftscript.utility;

public interface Entries {

    <Type> Type get(String key);

    <Type> Type getOrDefault(String key, Type alternative);

    <Type> Type get(String key, Class<Type> type);

    <Type> Type getOrDefault(String key, Class<Type> type, Type alternative);

}
