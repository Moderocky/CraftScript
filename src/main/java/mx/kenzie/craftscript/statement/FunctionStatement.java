package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record FunctionStatement(Statement<?> executable) implements Statement<Object>, EvaluatedStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return executable;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        stream.print("executable=");
        this.executable.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("function ");
        this.executable.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("function ", profile.dark()),
            Component.space(),
            this.executable.prettyPrint(profile)
        ).hoverEvent(Component.text("Creates an executable function object."));
    }

    @Override
    public Class<?> returnType() {
        return Statement.class;
    }

    @Override
    public Class<?> evaluatedReturnType() {
        return executable.returnType();
    }

}
