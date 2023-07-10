package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record LiteralStringStatement(String value) implements TextStatement {

    @Override
    public String execute(Context context) throws ScriptError {
        return value;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("value=");
        stream.print(value);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print('"');
        stream.print(value);
        stream.print('"');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final Component component = Component.textOfChildren(Component.text("Text.", profile.light()),
            Component.newline(), this.printReturnType(profile));
        return Component.textOfChildren(Component.text('"', profile.pop()), Component.text(value, profile.light()),
            Component.text('"', profile.pop())).hoverEvent(component);
    }

}
