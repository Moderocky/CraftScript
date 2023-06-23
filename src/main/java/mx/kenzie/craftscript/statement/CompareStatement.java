package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Comparator;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record CompareStatement(Statement<?> antecedent, Statement<?> consequent,
                               Comparator comparator) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        final Object a = Wrapper.unwrap(antecedent.execute(context)), b = Wrapper.unwrap(consequent.execute(context));
        return comparator.compare(a, b);
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

    @Override
    public void stringify(PrintStream stream) {
        this.antecedent.stringify(stream);
        stream.print(comparator.toString());
        this.consequent.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            this.antecedent.prettyPrint(profile),
            Component.space(),
            Component.text(comparator.toString(), profile.pop()),
            Component.space(),
            this.consequent.prettyPrint(profile)
        );
    }

}
