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
            case "type" -> this;
            case "type_name" -> type.getSimpleName();
            default -> null;
        };
    }

}
