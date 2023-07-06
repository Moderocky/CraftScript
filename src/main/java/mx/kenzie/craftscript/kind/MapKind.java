package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static mx.kenzie.craftscript.kind.Kinds.ANY;
import static mx.kenzie.craftscript.kind.Kinds.STRING;

public class MapKind extends Kind<Map> {

    public static final MapKind MAP = new MapKind();

    private volatile int step;

    public MapKind() {
        super(Map.class);
    }

    @Override
    public Object getProperty(Map thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "keys" -> new ArrayList<>(thing.keySet());
            case "values" -> new ArrayList<>(thing.values());
            case "size" -> thing.size();
            case "is_empty" -> thing.isEmpty();
            case "clear" -> CheckedFunction.ofVoid().runs(thing::clear);
            case "contains_key" -> CheckedFunction.ofNoContext(STRING).runs(thing::containsKey);
            case "contains_value" -> CheckedFunction.ofNoContext(ANY).runs(thing::containsValue);
            case "set" -> CheckedFunction.ofNoContext(STRING, ANY).runs(thing::put);
            default -> thing.get(property);
        };
    }

    @Override
    public Object setProperty(Map thing, String property, Object value) {
        if (thing == null) return null;
        return thing.put(property, value);
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "keys", "values", "size", "is_empty", "clear", "contains_key", "contains_value", "set"};
    }

    @Override
    public String toString(Map map) {
        final Iterator iterator = map.entrySet().iterator();
        if (!iterator.hasNext()) return "[]";
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        synchronized (this) {
            this.step++;
        }
        try {
            while (iterator.hasNext()) {
                final Map.Entry e = (Map.Entry) iterator.next();
                final Object key = e.getKey(), value = e.getValue();
                if (step < 8) {
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
