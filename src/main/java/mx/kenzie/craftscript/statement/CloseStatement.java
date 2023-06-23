package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record CloseStatement() implements Statement<Void> {

    @Override
    public Void execute(Context context) throws ScriptError {
        throw new ScriptError("This should not be used here.");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("}");
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text('}', profile.pop());
    }

}
