package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record IfElseStatement(Statement<?> check,
                              ChoiceBlockStatement choice) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Object result = this.check.execute(context);
        final boolean test = IfStatement.value(result);
        if (test) choice.either(context);
        else choice.or(context);
        return test;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("check=");
        this.check.debug(stream);
        stream.print(", ");
        stream.print("choice=");
        this.choice.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("if ");
        this.check.stringify(stream);
        stream.print(' ');
        this.choice.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("if ", profile.dark()),
            this.check.prettyPrint(profile),
            Component.space(),
            this.choice.prettyPrint(profile)
        ).hoverEvent(Component.text("If the condition is true, do..."));
    }

    @Override
    public Class<? extends Boolean> returnType() {
        return Boolean.class;
    }

}
