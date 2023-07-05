package mx.kenzie.craftscript.variable;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PropertyVariableContainer<Type> extends VariableContainer {

    private final Set<String> properties;
    private final Wrapper<Type> wrapper;

    public PropertyVariableContainer(VariableContainer parent, Wrapper<Type> wrapper) {
        super(parent.container, parent.parameters);
        this.wrapper = wrapper;
        this.properties = new HashSet<>(List.of(wrapper.kind.getProperties()));
    }

    @Override
    public Object get(Object object) {
        final String key = Objects.toString(object);
        if (properties.contains(key)) return wrapper.kind.getProperty(wrapper.thing, key);
        else return super.get(key);
    }

    @Override
    public @Nullable Object put(String key, Object value) {
        if (properties.contains(key)) return wrapper.kind.setProperty(wrapper.thing, key, value);
        else return super.put(key, value);
    }

}
