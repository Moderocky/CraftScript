package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.UnknownObject;

import java.util.ArrayList;

public class CustomKind extends Kind<UnknownObject> {

    public CustomKind() {
        super(UnknownObject.class);
    }

    @Override
    public Object getProperty(UnknownObject thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "properties" -> new ArrayList<>(thing.keySet());
            default -> thing.get(property);
        };
    }

    @Override
    public Object setProperty(UnknownObject thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            default -> thing.put(property, value);
        };
    }

    @Override
    public UnknownObject fromString(String string) {
        return null;
    }

    @Override
    public String toString(UnknownObject unknownObject) {
        return unknownObject.toString();
    }

}
