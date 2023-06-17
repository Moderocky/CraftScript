package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;

public record PlusStatement(Statement<?> antecedent, Statement<?> consequent) implements Statement<Object> {

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
        stream.print('+');
        this.consequent.stringify(stream);
    }

}
