package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.core.CheckedFunction;
import mx.kenzie.craftscript.script.core.InternalStatement;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mx.kenzie.craftscript.kind.Kinds.ANY;

public class KindKind extends Kind<Kind<?>> {

    public static final KindKind KIND = new KindKind();

    @SuppressWarnings("unchecked")
    public KindKind() {
        super((Class<Kind<?>>) (Object) Kind.class);
    }

    private static List<Object> list(Object[] objects) {
        if (objects == null || objects.length == 0) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(objects));
    }

    static <Type> Wrapper<Type> convertTo(Object object, Kind<Type> kind) {
        return new Wrapper<>(kind.convert(object), kind);
    }

    @Override
    public Object getProperty(Kind<?> thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "name" -> thing.type.getSimpleName();
            case "path" -> thing.type.getName();
            case "properties" -> list(Kind.asKind(thing).getProperties());
            case "is_array" -> thing.type.isArray();
            case "is_flag" -> thing.type.isEnum();
            case "values" -> list(thing.type.getEnumConstants());
            case "is_instance" -> CheckedFunction.of(ANY).runs(thing.type::isInstance);
            case "convert" -> new InternalStatement(arguments -> {
                if (arguments.size() > 1) {
                    final List<Wrapper<?>> result = new ArrayList<>(arguments.size());
                    for (final Object argument : arguments) result.add(convertTo(argument, thing));
                    return result;
                } else if (arguments.size() == 1) return convertTo(arguments.get(0), thing);
                else throw new ScriptError("Convert function expects at least one argument.");
            });
            default -> null;
        };
    }

    @Override
    public Class<?> getTypeHint(String property) {
        return switch (property) {
            case "convert" -> type;
            case "is_instance", "is_flag", "is_array" -> Boolean.class;
            case "values", "properties" -> List.class;
            default -> super.getTypeHint(property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "name", "path", "properties", "is_array", "is_flag", "values", "is_instance", "convert"};
    }

    @Override
    public String toString(Kind<?> string) {
        if (string.type == Kind.class) return this.toString();
        return string.toString();
    }

    @Override
    public String toStringTry(Object object) {
        if (object instanceof Kind<?> type) return this.toString(type);
        if (object instanceof Class) return this.toString();
        return super.toStringTry(object);
    }

    @Override
    public String toString() {
        return "#kind";
    }

}
