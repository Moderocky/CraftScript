package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;
import mx.kenzie.craftscript.utility.Container;
import mx.kenzie.craftscript.variable.ContainerMap;
import mx.kenzie.craftscript.variable.Wrapper;

import static mx.kenzie.craftscript.kind.Kinds.ANY;
import static mx.kenzie.craftscript.kind.Kinds.STRING;

public class ContainerKind<Type extends Container> extends MapKind<String, Object, Type> {

    public static final ContainerKind<Container> CONTAINER = new ContainerKind<>(Container.class);

    protected ContainerKind(Class<? super Type> type) {
        super((Class<Type>) type, StringKind.STRING, UnknownKind.ANY);
    }

    public ContainerKind() {
        this(Container.class);
    }

    @Override
    public Object getProperty(Type thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "contains_key" -> CheckedFunction.of(STRING).runs(thing::containsKey);
            case "contains_value" -> CheckedFunction.of(ANY).runs(thing::containsValue);
            case "set" -> CheckedFunction.of(STRING, ANY).runs(thing::put);
            case "clone" -> new Wrapper<>(new ContainerMap(thing), CONTAINER);
            default -> super.getProperty(thing, property);
        };
    }

    @Override
    public Object setProperty(Type thing, String property, Object value) {
        return super.setProperty(thing, property, value);
    }

}
