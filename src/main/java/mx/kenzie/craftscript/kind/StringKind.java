package mx.kenzie.craftscript.kind;

public class StringKind extends Kind<String> {

    public static final StringKind STRING = new StringKind();

    public StringKind() {
        super(String.class);
    }

    @Override
    public Object getProperty(String thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "length" -> thing.length();
            case "is_empty" -> thing.isEmpty();
            case "is_blank" -> thing.isBlank();
            case "lowercase" -> thing.toLowerCase();
            case "uppercase" -> thing.toUpperCase();
            default -> null;
        };
    }

    @Override
    public Object setProperty(String thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "length" -> thing.length() == ((Number) value).intValue();
            case "char_at" -> thing.charAt(((Number) value).intValue()) + "";
            case "substring" -> thing.substring(((Number) value).intValue());
            case "index_of" -> thing.indexOf(((String) value));
            case "starts_with" -> thing.startsWith(((String) value));
            case "ends_with" -> thing.endsWith(((String) value));
            case "contains" -> thing.contains(((String) value));
            case "matches" -> thing.matches(((String) value));
            default -> null;
        };
    }

    @Override
    public <Theirs> String convert(Theirs object, Kind<? super Theirs> kind) {
        if (object != null) return kind.toString(object);
        return null;
    }

    @Override
    public String toString(String string) {
        return string;
    }

}
