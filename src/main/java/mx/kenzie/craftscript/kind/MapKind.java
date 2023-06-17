package mx.kenzie.craftscript.kind;

import java.util.ArrayList;
import java.util.Map;

public class MapKind extends Kind<Map> {

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
    public Map fromString(String string) {
        return null;
    }

}
