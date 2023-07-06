package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.StructObject;
import mx.kenzie.craftscript.variable.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomKind extends ContainerKind<StructObject> {

    public static final CustomKind STRUCTURE = new CustomKind();
    protected final StructObject object;

    public CustomKind() {
        this(null);
    }

    public CustomKind(StructObject object) {
        super(StructObject.class);
        this.object = object;
    }

    @NotNull
    static String[] getSpecialProperties(Map<String, Object> object, String... ours) {
        if (object != null) {
            final Set<String> set = object.keySet();
            set.addAll(List.of(ours));
            return set.toArray(new String[0]);
        } else return ours;
    }

    @Override
    public Object getProperty(StructObject thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "properties" -> new ArrayList<>(thing.keySet());
            case "clone" -> new Wrapper<>(new StructObject(thing), this);
            default -> thing.get(property);
        };
    }

    @Override
    public Object setProperty(StructObject thing, String property, Object value) {
        if (thing == null) return null;
        return thing.replace(property, value);
    }

    @Override
    public String[] getProperties() {
        return getSpecialProperties(object, "type", "properties", "clone");
    }

    @Override
    public String toString(StructObject unknownObject) {
        return unknownObject.toString();
    }

    @Override
    public String toString() {
        return "#structure";
    }

}
