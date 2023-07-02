package mx.kenzie.craftscript.kind;

public class IntegerKind extends NumberKind {

    public static final IntegerKind INTEGER = new IntegerKind();

    public IntegerKind() {
        super(Integer.class);
    }

    @Override
    public Object getProperty(Number thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "abs" -> Math.abs(thing.intValue());
            case "sqrt" -> (int) Math.sqrt(thing.intValue());
            case "floor", "round", "ceil" -> (int) thing;
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
        else return super.convert(object, kind).intValue();
    }

    @Override
    public Kind<?> superKind() {
        return NUMBER;
    }

}
