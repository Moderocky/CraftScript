package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.ScriptError;

public class IntegerKind extends NumberKind<Integer> {

    public static final IntegerKind INTEGER = new IntegerKind();

    public IntegerKind() {
        super(Integer.class);
    }

    @Override
    public Object getProperty(Integer thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "abs" -> Math.abs(thing);
            case "sqrt" -> (int) Math.sqrt(thing);
            case "floor", "round", "ceil" -> thing;
            default -> super.getProperty(thing, property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"abs", "sqrt", "type", "integer", "decimal", "abs", "sqrt", "floor", "ceil", "round"};
    }

    @Override
    public <Theirs> Integer convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Number number) return number.intValue();
        else if (object == null) return 0;
        else if (object instanceof String string) return this.valueOf(string);
        else return super.convert(object, kind);
    }

    @Override
    public Kind<?> superKind() {
        return NUMBER;
    }

    private Integer valueOf(String string) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException ex) {
            throw new ScriptError("Cannot convert '" + string + "' to an integer.");
        }
    }

}
