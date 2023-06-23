package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record InvertStatement(Statement<?> check) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Object result = this.check.execute(context);
        return !IfStatement.value(result);
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
        stream.print("!");
        this.check.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text('!', profile.pop()),
            this.check.prettyPrint(profile)
        );
    }

}
