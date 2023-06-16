package mx.kenzie.craftscript.kind;

import java.util.Objects;

public class NumberKind extends Kind<Number> {

    public NumberKind() {
        super(Number.class);
    }

    @Override
    public Object getProperty(Number thing, String property) {
        if (Objects.equals(property, "type")) return this;
        return null;
    }

    @Override
    public Number fromString(String string) {
        try {
            return string.contains(".") ? Double.parseDouble(string) : Integer.parseInt(string);
        } catch (Throwable ex) {
            return 0;
        }
    }

}
