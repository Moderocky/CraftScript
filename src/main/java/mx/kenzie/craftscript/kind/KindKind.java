package mx.kenzie.craftscript.kind;

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
            case "is_array" -> thing.isArray();
            case "is_enum" -> thing.isEnum();
            default -> null;
        };
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
    public String toString(Class string) {
        if (string == Kind.class || string == Class.class) return "#class";
        return Kind.of(string).toString();
    }

    @Override
    public String toStringTry(Object object) {
        if (object instanceof Kind<?> type) return this.toString(type.getType());
        if (object instanceof Class type) return this.toString(type);
        return super.toStringTry(object);
    }

}
