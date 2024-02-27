package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Container;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class PropertyVariableContainer<Type> extends VariableContainer implements Container {

    private final Set<String> properties;
    private final Wrapper<Type> wrapper;
    private final Map<String, Supplier<Object>> overrides;

    public PropertyVariableContainer(VariableContainer parent, Wrapper<Type> wrapper) {
        super(parent.container, parent.parameters);
        this.wrapper = wrapper;
        this.properties = new HashSet<>(List.of(wrapper.kind.getProperties()));
        this.overrides = new HashMap<>();
    }

    public Object getProperty(String key) {
        if (properties.contains(key)) return wrapper.kind.getProperty(wrapper.thing, key);
        else if (overrides.containsKey(key)) return overrides.get(key).get();
        else throw new ScriptError("No local keyword '" + key + "' is available here.");
    }

    @Override
    public boolean containsKey(Object key) {
        if (properties.contains(key)) return true;
        if (overrides.containsKey(key)) return true;
        return super.containsKey(key);
    }

    @Override
    public Object get(Object object) {
        final String key = Objects.toString(object);
        if (properties.contains(key)) return wrapper.kind.getProperty(wrapper.thing, key);
        else if (overrides.containsKey(key)) return overrides.get(key).get();
        else return super.get(key);
    }

    @Override
    public @Nullable Object put(String key, Object value) {
        if (properties.contains(key)) return wrapper.kind.setProperty(wrapper.thing, key, value);
        else return super.put(key, value);
    }

    public void putOverride(String key, Supplier<Object> value) {
        this.overrides.put(key, value);
    }

    public void removeOverride(String key) {
        this.overrides.remove(key);
    }

}
