package mx.kenzie.craftscript.utility;

public interface Entries {

    <Type> Type getValue(String key);

    <Type> Type getValueOr(String key, Type alternative);

    <Type> Type getValue(String key, Class<Type> type);

    <Type> Type getValueOr(String key, Class<Type> type, Type alternative);

}
