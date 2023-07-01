package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.StructObject;

import java.util.ArrayList;

public class CustomKind extends Kind<StructObject> {

    public static final CustomKind STRUCTURE = new CustomKind();

    public CustomKind() {
        super(StructObject.class);
    }

    @Override
    public Object getProperty(StructObject thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "properties" -> new ArrayList<>(thing.keySet());
            default -> thing.get(property);
        };
    }

    @Override
    public Object setProperty(StructObject thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            default -> thing.replace(property, value);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "properties"};
    }

    @Override
    public String toString(StructObject unknownObject) {
        return unknownObject.toString();
    }

}
