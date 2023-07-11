package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.Objects;

public record EqualsStatement(Statement<?> antecedent, Statement<?> consequent) implements BinaryStatement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Object a = Wrapper.unwrap(antecedent.execute(context)), b = Wrapper.unwrap(consequent.execute(context));
        return execute(a, b);
    }

    public static Boolean execute(Object a, Object b) {
        return Objects.equals(a, b)
            || (a instanceof Number first && b instanceof Number second && first.doubleValue() == second.doubleValue());
    }

    @Override
    public String symbol() {
        return "==";
    }

    @Override
    public Class<? extends Boolean> returnType() {
        return Boolean.class;
    }

}
