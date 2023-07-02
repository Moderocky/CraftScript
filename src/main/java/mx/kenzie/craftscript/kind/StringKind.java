package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.core.InternalStatement;

import static mx.kenzie.craftscript.kind.IntegerKind.INTEGER;

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
            case "char_at" ->
                new InternalStatement(arguments -> thing.charAt((int) INTEGER.convert(arguments.get(0))) + "");
            case "substring" -> new InternalStatement(arguments -> {
                if (arguments.size() == 1) return thing.substring((int) INTEGER.convert(arguments.get(0)));
                else if (arguments.size() == 2) return thing.substring((int) INTEGER.convert(arguments.get(0)),
                    (int) INTEGER.convert(arguments.get(1)));
                else throw new ScriptError("Substring function takes 1 or 2 integers.");
            });
            case "index_of" -> new InternalStatement(arguments -> thing.indexOf(STRING.convert(arguments.get(0))));
            case "starts_with" ->
                new InternalStatement(arguments -> thing.startsWith(STRING.convert(arguments.get(0))));
            case "ends_with" -> new InternalStatement(arguments -> thing.endsWith(STRING.convert(arguments.get(0))));
            case "contains" -> new InternalStatement(arguments -> thing.contains(STRING.convert(arguments.get(0))));
            case "matches" -> new InternalStatement(arguments -> thing.matches(STRING.convert(arguments.get(0))));
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "length", "is_empty", "is_blank", "lowercase", "uppercase", "char_at", "substring", "index_of", "starts_with", "ends_with", "contains", "matches"};
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
