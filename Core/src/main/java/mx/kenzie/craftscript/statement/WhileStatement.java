package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record WhileStatement(Statement<?> check, Statement<?> then) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        boolean run = false;
        while (IfStatement.value(this.check.execute(context))) {
            this.then.execute(context);
            run = true;
        }
        return run;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("check=");
        this.check.debug(stream);
        stream.print(", ");
        stream.print("then=");
        this.then.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("while ");
        this.check.stringify(stream);
        stream.print(' ');
        this.then.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("while ", profile.dark()),
            this.check.prettyPrint(profile),
            Component.space(),
            this.then.prettyPrint(profile)
        ).hoverEvent(Component.textOfChildren(
            Component.text("Runs a statement as long as the condition is true.", profile.light()),
            Component.newline(),
            this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Boolean> returnType() {
        return Boolean.class;
    }

}
