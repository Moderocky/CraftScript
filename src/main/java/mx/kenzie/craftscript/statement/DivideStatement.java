package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

public record DivideStatement(Statement<?> antecedent, Statement<?> consequent) implements BinaryStatement<Number> {

    @Override
    public Number execute(Context context) throws ScriptError {
        final Object a = Wrapper.unwrap(antecedent.execute(context)), b = Wrapper.unwrap(consequent.execute(context));
        if (!(a instanceof Number first)) return null;
        if (!(b instanceof Number second)) return null;
        if (first instanceof Integer && second instanceof Integer) return first.intValue() / second.intValue();
        return first.doubleValue() / second.doubleValue();
    }

    @Override
    public String symbol() {
        return "/";
    }

}
