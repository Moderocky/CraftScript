package mx.kenzie.craftscript.kind;

public class UnknownKind extends Kind<Object> {

    @SuppressWarnings("unchecked")
    public UnknownKind(Class<?> type) {
        super((Class<Object>) type);
    }

    @Override
    public Object getProperty(Object thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.of(thing));
            case "type_name" -> type.getSimpleName();
            default -> null;
        };
    }

    @Override
    public Object fromString(String string) {
        return null;
    }

}
