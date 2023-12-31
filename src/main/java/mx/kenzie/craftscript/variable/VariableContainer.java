package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.utility.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class VariableContainer implements Map<String, Object>, Container {

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

}
