package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record LineStatement(Statement<?> statement, int line, String indent) implements Statement<Object> {

    public LineStatement(Statement<?> statement, int line) {
        this(statement, line, "");
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        context.data().line = this;
        return statement.execute(context);
    }

    @Override
    public void debug(PrintStream stream) {
        this.statement.debug(stream);
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(indent);
        this.statement.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text(indent, profile.light()),
            this.statement.prettyPrint(profile)
        ).hoverEvent(Component.text("Line " + line, profile.light()));
    }

}
