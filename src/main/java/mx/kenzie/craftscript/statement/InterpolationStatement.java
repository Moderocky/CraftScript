package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record InterpolationStatement(String key, Statement<?> statement) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return statement.execute(context);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("key=");
        stream.print(key);
        stream.print(", ");
        stream.print("value=");
        this.statement.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print('{');
        this.statement.stringify(stream);
        stream.print('}');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text('{', profile.pop()),
            this.statement.prettyPrint(profile),
            Component.text('}', profile.pop())
        );
    }

}
