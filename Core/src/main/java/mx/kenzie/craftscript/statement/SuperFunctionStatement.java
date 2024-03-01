package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.core.SuperFunction;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;
import java.util.List;

public record SuperFunctionStatement(List<Statement<?>> header,
                                     Statement<?> executable) implements Statement<Object>, EvaluatedStatement<Object> {

    @Override
    public SuperFunction<?> execute(Context<?> context) throws ScriptError {
        return new SuperFunction<>(executable, header.toArray(new Statement[0]));
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        stream.print("header=[");
        boolean first = true;
        for (Statement<?> statement : header) {
            if (first) first = false;
            else stream.print(',');
            statement.debug(stream);
        }
        stream.print("],");
        stream.print("executable=");
        this.executable.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("fn(");
        boolean first = true;
        for (Statement<?> statement : header) {
            if (first) first = false;
            else stream.print(", ");
            statement.stringify(stream);
        }
        stream.print(") ");
        this.executable.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("fn", profile.dark()),
            Component.text("(", profile.pop()),
            // todo
            Component.text(")", profile.pop()),
            Component.space(),
            this.executable.prettyPrint(profile)
        ).hoverEvent(Component.textOfChildren(Component.text("Creates an executable function object.", profile.light()),
            Component.newline(), this.printReturnType(profile)));
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
