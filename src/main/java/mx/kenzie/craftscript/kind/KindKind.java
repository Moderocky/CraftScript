package mx.kenzie.craftscript.kind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class KindKind extends Kind<Class> {

    public static final KindKind KIND = new KindKind();

    public KindKind() {
        super(Class.class);
    }

    @Override
    public Object getProperty(Class thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "name" -> thing.getSimpleName();
            case "path" -> thing.getName();
            case "properties" -> new HashSet<>(List.of(Kind.asKind(thing).getProperties()));
            case "is_array" -> thing.isArray();
            case "is_flag" -> thing.isEnum();
            case "values" -> list(thing.getEnumConstants());
            default -> null;
        };
    }

    private static List<Object> list(Object[] objects) {
        if (objects == null || objects.length == 0) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(objects));
    }

    @Override
    public Object setProperty(Class thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "name" -> thing.getSimpleName().equals(value + "");
            case "path" -> thing.getName().equals(value + "");
            case "is_instance" -> thing.isInstance(value);
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "name", "path", "properties", "is_array", "is_enum"};
    }

    @Override
    public String toString(Class string) {
        if (string == Kind.class || string == Class.class) return this.toString();
        return Kind.of(string).toString();
    }

    @Override
    public String toStringTry(Object object) {
        if (object instanceof Kind<?> type) return this.toString(type.getType());
        if (object instanceof Class type) return this.toString(type);
        return super.toStringTry(object);
    }

    @Override
    public String toString() {
        return "#kind";
    }

}
