package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.utility.Container;

import java.util.LinkedHashMap;
import java.util.Map;

public class ContainerMap extends LinkedHashMap<String, Object> implements Container {

    public ContainerMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ContainerMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ContainerMap() {
        super();
    }

    public ContainerMap(Map<? extends String, ?> m) {
        super(m);
    }

    public ContainerMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

}
