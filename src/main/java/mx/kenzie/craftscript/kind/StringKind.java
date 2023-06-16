package mx.kenzie.craftscript.kind;

public class StringKind extends Kind<String> {

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
            case "is_blank" -> thing.isEmpty();
            case "lowercase" -> thing.toLowerCase();
            case "uppercase" -> thing.toUpperCase();
            default -> null;
        };
    }

    @Override
    public Object setProperty(String thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.of(value));
            case "equals" -> thing.equals(value);
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
    public String fromString(String string) {
        return string;
    }

    @Override
    public String toString(String string) {
        return string;
    }

}
