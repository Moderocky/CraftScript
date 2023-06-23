package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record LiteralStatement(Object value) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return Wrapper.of(value);
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
        stream.print(value);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text(Wrapper.of(value).toString(), profile.light())
            .hoverEvent(Component.text("A literal value.", profile.light()));
    }

}
