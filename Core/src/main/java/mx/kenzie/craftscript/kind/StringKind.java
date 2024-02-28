package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;

import static mx.kenzie.craftscript.kind.Kinds.INTEGER;

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
            case "char_at" -> CheckedFunction.of(INTEGER).runs(index -> thing.charAt(index) + "");
            case "substring" -> CheckedFunction.of(INTEGER, INTEGER)
                .defaultValues(0, thing.length())
                .runs(thing::substring);
            case "index_of" -> CheckedFunction.of(STRING).runs(thing::indexOf);
            case "starts_with" -> CheckedFunction.of(STRING).runs(thing::startsWith);
            case "ends_with" -> CheckedFunction.of(STRING).runs(thing::endsWith);
            case "contains" -> CheckedFunction.of(STRING).runs(thing::contains);
            case "matches" -> CheckedFunction.of(STRING).runs(thing::matches);
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[] {"type", "length", "is_empty", "is_blank", "lowercase", "uppercase", "char_at", "substring", "index_of", "starts_with", "ends_with", "contains", "matches"};
    }

    @Override
    public <Theirs> String convert(Theirs object, Kind<? super Theirs> kind) {
        if (object != null) return kind.toString(object);
        return "null";
    }

    @Override
    public String toString(String string) {
        return string;
    }

}
