package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.ScriptError;

public class NumberKind<Type extends Number> extends Kind<Type> {

    public static final NumberKind<Number> NUMBER = new NumberKind<>();

    @SuppressWarnings("unchecked")
    public NumberKind() {
        this((Class<Type>) Number.class);
    }

    protected NumberKind(Class<Type> type) {
        super(type);
    }

    @Override
    public Object getProperty(Type thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "integer" -> thing.intValue();
            case "decimal" -> thing.doubleValue();
            case "abs" -> Math.abs(thing.doubleValue());
            case "sqrt" -> Math.sqrt(thing.doubleValue());
            case "floor" -> Math.floor(thing.doubleValue());
            case "ceil" -> Math.ceil(thing.doubleValue());
            case "round" -> Math.round(thing.doubleValue());
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "integer", "decimal", "abs", "sqrt", "floor", "ceil", "round"};
    }

    @Override
    public <Theirs> Type convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Number number) return (Type) number;
        if (object == null) return (Type) (Double) 0.0;
        else if (object instanceof String string) return this.valueOf(string);
        else return super.convert(object, kind);
    }

    private Type valueOf(String string) {
        try {
            return (Type) Double.valueOf(string);
        } catch (NumberFormatException ex) {
            throw new ScriptError("Cannot convert '" + string + "' to a number.");
        }
    }

}
