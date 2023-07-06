package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Comparator;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;

public record CompareStatement(Statement<?> antecedent, Statement<?> consequent,
                               Comparator comparator) implements BinaryStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        final Object a = Wrapper.unwrap(antecedent.execute(context)), b = Wrapper.unwrap(consequent.execute(context));
        return comparator.compare(a, b);
    }

    @Override
    public String symbol() {
        return comparator.toString();
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
        stream.print(", ");
        stream.print("comparator=");
        stream.print(comparator.toString());
        stream.print(']');
    }

}
