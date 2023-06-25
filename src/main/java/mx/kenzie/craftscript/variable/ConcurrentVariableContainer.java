package mx.kenzie.craftscript.variable;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentVariableContainer extends VariableContainer {

    public ConcurrentVariableContainer() {
        this(new ArrayList<>(4));
    }

    protected ConcurrentVariableContainer(Collection<Object> parameters) {
        super(new ConcurrentHashMap<>(), parameters);
    }

    public static ConcurrentVariableContainer noParameters() {
        return new ConcurrentVariableContainer(Collections.emptyList());
    }

    @Override
    public Object get(Object key) {
        if (key == null) return null;
        return super.get(key);
    }

    @Override
    public @Nullable Object put(String key, Object value) {
        if (key == null) return null;
        if (value == null) return super.remove(key);
        return super.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        if (key == null) return null;
        return super.remove(key);
    }

}
