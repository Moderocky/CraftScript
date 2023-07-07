package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record NullStatement() implements Statement<Void> {

    @Override
    public Void execute(Context context) throws ScriptError {
        return null;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("null");
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text("null", profile.dark())
            .hoverEvent(
                Component.textOfChildren(Component.text("An empty value.", profile.light()), Component.newline(),
                    this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Void> returnType() {
        return Void.class;
    }

}
