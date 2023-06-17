package mx.kenzie.craftscript.variable;

public class VariableFinder extends VariableContainer {

    protected final VariableContainer container;

    public VariableFinder(VariableContainer container) {
        this.container = container;
    }

    @Override
    public Object get(Object key) {
        final Object object = super.get(key);
        if (object != null) return object;
        return container.get(key);
    }

}
