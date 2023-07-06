package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class MapKind<Key, Value, Type extends Map<Key, Value>> extends Kind<Type> {

    public static final MapKind<Object, Object, Map<Object, Object>> MAP = new MapKind<Object, Object, Map<Object, Object>>(
        Map.class, UnknownKind.ANY, UnknownKind.ANY);
    protected final Kind<Key> keyKind;
    protected final Kind<Value> valueKind;
    private volatile int step;

    @SuppressWarnings("unchecked")
    private MapKind(Object object, Object keyKind, Object valueKind) {
        this((Class<Type>) object, (Kind<Key>) keyKind, (Kind<Value>) valueKind);
    }

    protected MapKind(Class<Type> type, Kind<Key> keyKind, Kind<Value> valueKind) {
        super(type);
        this.keyKind = keyKind;
        this.valueKind = valueKind;
    }

    @Override
    public Object getProperty(Type thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "keys" -> new LinkedHashSet<>(thing.keySet());
            case "values" -> new LinkedHashSet<>(thing.values());
            case "size" -> thing.size();
            case "is_empty" -> thing.isEmpty();
            case "clear" -> CheckedFunction.ofVoid().runs(thing::clear);
            case "contains_key" -> CheckedFunction.of(keyKind).runs(thing::containsKey);
            case "contains_value" -> CheckedFunction.of(valueKind).runs(thing::containsValue);
            case "set" -> CheckedFunction.of(keyKind, valueKind).runs(thing::put);
            case "clone" -> new Wrapper<>(new LinkedHashMap<>(thing), MAP);
            default -> thing.get(keyKind.convert(property));
        };
    }

    @Override
    public Object setProperty(Type thing, String property, Object value) {
        if (thing == null) return null;
        return thing.put(keyKind.convert(property), valueKind.convert(value));
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "keys", "values", "size", "is_empty", "clear", "contains_key", "contains_value", "set", "clone"};
    }

    @Override
    public String toString(Type map) {
        final Iterator<Map.Entry<Key, Value>> iterator = map.entrySet().iterator();
        if (!iterator.hasNext()) return "[]";
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        synchronized (this) {
            this.step++;
        }
        try {
            while (iterator.hasNext()) {
                final Map.Entry<Key, Value> entry = iterator.next();
                final Object key = entry.getKey(), value = entry.getValue();
                if (step < 6) {
                    builder.append(Wrapper.unwrap(key) == map ? "<this>" : key);
                    builder.append('=');
                    builder.append(Wrapper.unwrap(value) == map ? "<this>" : value);
                } else {
                    builder.append("???=???");
                }
                if (!iterator.hasNext()) return builder.append(']').toString();
                builder.append(',').append(' ');
            }
        } finally {
            synchronized (this) {
                this.step--;
            }
        }
        return builder.append(']').toString();
    }

}
