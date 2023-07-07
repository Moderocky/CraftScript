package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record AssertStatement(Statement<?> check) implements Statement<Boolean> {

    @Override
    public Class<Boolean> returnType() {
        return Boolean.class;
    }

    @Override
    public boolean knowsReturnType() {
        return true;
    }

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Object result = Wrapper.unwrap(this.check.execute(context));
        final boolean test = IfStatement.value(result);
        if (test) return true;
        if (result instanceof Boolean)
            throw new ScriptError("The statement '" + check.stringify() + "' returned '" + Wrapper.of(result)
                .toString() + "'.");
        else throw new ScriptError("The statement '" + check.stringify() + "' returned '" + Wrapper.of(result)
            .toString() + "' which evaluated to false.");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("check=");
        this.check.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("assert ");
        this.check.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("assert ", profile.dark()),
            this.check.prettyPrint(profile)
        ).hoverEvent(Component.textOfChildren(Component.text("Make sure a statement is true.", profile.light()),
            this.printReturnType(profile)));
    }

}
