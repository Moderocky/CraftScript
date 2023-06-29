package mx.kenzie.craftscript.kind;

public class EnumKind<Type extends Enum<Type>> extends Kind<Enum<Type>> {

    public EnumKind(Class<Enum<Type>> type) {
        super(type);
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
    public String toString(Enum<Type> typeEnum) {
        return typeEnum.name().toLowerCase();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enum<Type> fromString(String string) {
        return (Enum<Type>) Enum.valueOf((Class<? extends Enum>) type, string.toUpperCase());
    }

}
