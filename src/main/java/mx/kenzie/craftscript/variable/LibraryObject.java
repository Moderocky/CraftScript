package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.kind.LibraryKind;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LibraryObject extends StructObject implements Map<String, Object>, Container {

    public static final LibraryKind LIBRARY = new LibraryKind();

    protected final Set<String> keys;

    protected LibraryObject(String... keys) {
        this.keys = new HashSet<>(List.of(keys));
        this.freeze();
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return keys.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String string) return this.get(string);
        return this.get(Objects.toString(key));
    }

    public abstract Object get(String key);

    @Nullable
    @Override
    public Object put(String key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        throw new ScriptError("Attempted to edit library object.");
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        throw new ScriptError("Attempted to edit library object.");
    }

    @Override
    public void clear() {
        throw new ScriptError("Attempted to edit library object.");
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return new HashSet<>(keys);
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        final Set<Entry<String, Object>> set = new HashSet<>();
        for (String key : keys) set.add(new Property(key, this.get(key)));
        return set;
    }

    public boolean verify() { // for testing
        for (String key : keys) if (this.get(key) == null) return false;
        return true;
    }

    record Property(String getKey, Object getValue) implements Entry<String, Object> {

        @Override
        public Object setValue(Object value) {
            throw new ScriptError("Unable to change library function.");
        }

    }

}
