package mx.kenzie.craftscript.kind;

public class EnumKind<Type extends Enum<Type>> extends Kind<Type> {

    public static final EnumKind<?> ENUM = new EnumKind<>(Enum.class, null);

    @SuppressWarnings("unchecked")
    protected EnumKind(Class<? extends Enum<Type>> type) {
        super((Class<Type>) type);
    }

    @SuppressWarnings("unchecked")
    private EnumKind(Object object, Void unused) {
        this((Class<Type>) object);
    }

    @Override
    public Object getProperty(Type thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "name" -> thing.name();
            case "index" -> thing.ordinal();
            case "type" -> this;
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"name", "index", "type"};
    }

    @Override
    public <Theirs> Type convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof String string) return Enum.valueOf(type, string);
        return super.convert(object, kind);
    }

    @Override
    public String toString(Type typeEnum) {
        return typeEnum.name().toLowerCase();
    }

    @Override
    public String toString() {
        return "#flag";
    }

}
