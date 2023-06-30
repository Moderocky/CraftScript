package mx.kenzie.craftscript.kind;

public class IntegerKind extends NumberKind {
    public IntegerKind() {
        super(Integer.class);
    }

    @Override
    public <Theirs> Integer convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Number number) return number.intValue();
        else return super.convert(object, kind).intValue();
    }
}
