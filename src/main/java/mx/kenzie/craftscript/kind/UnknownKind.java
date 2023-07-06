package mx.kenzie.craftscript.kind;

public class UnknownKind extends Kind<Object> {

    public static final UnknownKind ANY = new UnknownKind(Object.class);

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

    @Override
    public <Theirs> Object convert(Theirs object, Kind<? super Theirs> kind) {
        if (type == Object.class) return object;
        return super.convert(object, kind);
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "type_name"};
    }

}
