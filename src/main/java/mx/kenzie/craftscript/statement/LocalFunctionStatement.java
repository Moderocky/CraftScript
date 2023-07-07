package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record LocalFunctionStatement(Statement<?> statement, Statement<?> data) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return RunStatement.execute(context, statement, data);
    }

    @Override
    public void debug(PrintStream stream) {
        RunStatement.debug(stream, this.getClass(), statement, data);
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("run ");
        this.statement.stringify(stream);
        if (data != null) {
            stream.print(' ');
            this.data.stringify(stream);
        }
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        if (data == null) return Component.textOfChildren(
            this.statement.prettyPrint(profile)
        ).hoverEvent(Component.text("Run an executable task.", profile.light()));
        else return Component.textOfChildren(
            this.statement.prettyPrint(profile),
            Component.space(),
            this.data.prettyPrint(profile)
        ).hoverEvent(Component.text("Run an executable task with inputs.", profile.light()));
    }

    @Override
    public Class<?> returnType() {
        if (statement instanceof EvaluatedStatement<?> evaluated) return evaluated.evaluatedReturnType();
        return statement.returnType();
    }

}
