package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record TimesStatement(Statement<?> antecedent, Statement<?> consequent) implements Statement<Number> {

    @Override
    public Number execute(Context context) throws ScriptError {
        final Object a = Wrapper.unwrap(antecedent.execute(context)), b = Wrapper.unwrap(consequent.execute(context));
        if (!(a instanceof Number first)) return null;
        if (!(b instanceof Number second)) return null;
        if (first instanceof Integer && second instanceof Integer) return first.intValue() * second.intValue();
        return first.doubleValue() * second.doubleValue();
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
        stream.print('*');
        this.consequent.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            this.antecedent.prettyPrint(profile),
            Component.space(),
            Component.text('*', profile.pop()),
            Component.space(),
            this.consequent.prettyPrint(profile)
        );
    }

}
