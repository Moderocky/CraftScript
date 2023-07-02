package mx.kenzie.craftscript.kind;

public class NumberKind extends Kind<Number> {

    public static final NumberKind NUMBER = new NumberKind();

    public NumberKind() {
        super(Number.class);
    }

    @SuppressWarnings("unchecked")
    protected NumberKind(Class<? extends Number> type) {
        super((Class<Number>) type);
    }

    @Override
    public Object getProperty(Number thing, String property) {
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
    public <Theirs> Number convert(Theirs object, Kind<? super Theirs> kind) {
        if (object == null) return 0;
        else if (object instanceof String string) return Double.valueOf(string);
        else return super.convert(object, kind).intValue();
    }

}
