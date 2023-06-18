package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;
import java.util.Objects;

public record EqualsStatement(Statement<?> antecedent, Statement<?> consequent) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Object a = Wrapper.unwrap(antecedent.execute(context)), b = Wrapper.unwrap(consequent.execute(context));
        return Objects.equals(a, b)
            || (a instanceof Number first && b instanceof Number second && first.doubleValue() == second.doubleValue());
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("antecedent=");
        this.antecedent.debug(stream);
        stream.print(", ");
        stream.print("consequent=");
        this.consequent.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        this.antecedent.stringify(stream);
        stream.print("==");
        this.consequent.stringify(stream);
    }

}
