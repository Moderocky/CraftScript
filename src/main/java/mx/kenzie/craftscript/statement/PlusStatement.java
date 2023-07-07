package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

public record PlusStatement(Statement<?> antecedent, Statement<?> consequent) implements BinaryStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        final Object a = Wrapper.unwrap(antecedent.execute(context)), b = Wrapper.unwrap(consequent.execute(context));
        if (a instanceof String string) return string + b;
        if (b instanceof String string) return a + string;
        if (!(a instanceof Number first)) return null;
        if (!(b instanceof Number second)) return null;
        if (first instanceof Integer && second instanceof Integer) return first.intValue() + second.intValue();
        return first.doubleValue() + second.doubleValue();
    }

    @Override
    public String symbol() {
        return "+";
    }

    @Override
    public Class<?> returnType() {
        if (antecedent.returnType() == Integer.class && consequent.returnType() == Integer.class) return Integer.class;
        if (Number.class.isAssignableFrom(antecedent.returnType()) && Number.class.isAssignableFrom(
            consequent.returnType())) return Number.class;
        return String.class;
    }

    @Override
    public boolean knowsReturnType() {
        return true;
    }

}
