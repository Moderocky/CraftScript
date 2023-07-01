package mx.kenzie.craftscript.kind;

public class EnumKind<Type extends Enum<Type>> extends Kind<Enum<Type>> {

    public static final EnumKind<?> ENUM = new EnumKind<>();

    @SuppressWarnings("unchecked")
    public EnumKind(Class<? extends Enum<Type>> type) {
        super((Class<Enum<Type>>) type);
    }

    @SuppressWarnings("unchecked")
    public EnumKind() {
        super((Class<Enum<Type>>) (Object) Enum.class);
    }

    @Override
    public Object getProperty(Enum<Type> thing, String property) {
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
    @SuppressWarnings("unchecked")
    public <Theirs> Enum<Type> convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof String string) return Enum.valueOf((Class<Type>) type, string);
        return super.convert(object, kind);
    }

    @Override
    public String toString(Enum<Type> typeEnum) {
        return typeEnum.name().toLowerCase();
    }

}
