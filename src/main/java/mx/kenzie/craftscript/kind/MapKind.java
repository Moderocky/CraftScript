package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.Wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
            case "properties" -> new ArrayList<>(thing.keySet());
            case "size" -> thing.size();
            default -> thing.get(property);
        };
    }

    @Override
    public Object setProperty(Map thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            default -> thing.put(property, value);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "properties", "size"};
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
