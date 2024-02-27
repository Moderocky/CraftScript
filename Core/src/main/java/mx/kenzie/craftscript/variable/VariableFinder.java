package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.utility.Container;

public class VariableFinder extends VariableContainer implements Container {

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
