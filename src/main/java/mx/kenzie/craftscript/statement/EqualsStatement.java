package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.Objects;

public record EqualsStatement(Statement<?> antecedent, Statement<?> consequent) implements BinaryStatement<Boolean> {

    public static Boolean execute(Object antecedent, Object consequent) {
        final Object a = Wrapper.unwrap(antecedent), b = Wrapper.unwrap(consequent);
        return Objects.equals(a, b)
            || (a instanceof Number first && b instanceof Number second && first.doubleValue() == second.doubleValue());
    }

    @Override
    public Boolean execute(Context context) throws ScriptError {
        return execute(antecedent.execute(context), consequent.execute(context));
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
