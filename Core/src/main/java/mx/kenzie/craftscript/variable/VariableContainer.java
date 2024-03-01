package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.utility.Container;
import mx.kenzie.craftscript.utility.Entries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class VariableContainer implements Entries, Map<String, Object>, Container {

    public static final Pattern VAR_NAME = Pattern.compile("^[a-zA-Z_\\-.][a-zA-Z0-9_\\-.]*$");
    protected final Map<String, Object> container;
    protected final Collection<Object> parameters;

    protected VariableContainer(Map<String, Object> container, Collection<Object> parameters) {
        this.container = container;
        this.parameters = parameters;
    }

    public VariableContainer() {
        this(new HashMap<>(), new ArrayList<>(8));
    }

    public VariableContainer(Map<String, Object> container) {
        this(new HashMap<>(container), new ArrayList<>());
    }

    public Collection<Object> getParameters() {
        return parameters;
    }

    @Override
    public int size() {
        return container.size();
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return container.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return container.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return container.get(key);
    }

    @Nullable
    @Override
    public Object put(String key, Object value) {
        return container.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return container.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        this.container.putAll(m);
    }

    @Override
    public void clear() {
        this.container.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return container.keySet();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return container.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return container.entrySet();
    }

    public String debug() {
        final StringBuilder builder = new StringBuilder("[");
        int x = 0;
        for (Entry<String, Object> entry : this.entrySet()) {
            if (x++ > 0) builder.append(',');
            builder.append(entry.getKey()).append('=').append(entry.getValue());
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public String toString() {
        return this.debug();
    }


    @SuppressWarnings("unchecked")
    public <Type> Type get(String key) {
        return (Type) this.get((Object) key);
    }

    @SuppressWarnings("unchecked")
    public <Type> Type getOrDefault(String key, Type alternative) {
        try {
            return (Type) this.getOrDefault((Object) key, alternative);
        } catch (ClassCastException ex) {
            return alternative;
        }
    }

    @Nullable
    @Override
    public Object putIfAbsent(String key, Object value) {
        return Container.super.putIfAbsent(key, value);
    }

    public <Type> Type get(String key, Class<Type> type) {
        final Object found = this.get((Object) key);
        if (type.isInstance(found)) return type.cast(found);
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public <Type> Type getOrDefault(String key, Class<Type> type, Type alternative) {
        final Object found = this.getOrDefault((Object) key, alternative);
        if (type.isInstance(found)) return type.cast(found);
        return alternative;
    }

}
