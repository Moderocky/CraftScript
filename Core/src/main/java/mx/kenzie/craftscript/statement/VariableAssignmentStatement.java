package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.VariableHelper;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record VariableAssignmentStatement(String name, Statement<?> statement) implements Statement<Object> {

    public VariableAssignmentStatement {
        VariableHelper.instance().assign(name, statement);
    }

    @Override
    public Object execute(Context<?> context) throws ScriptError {
        final Object result = this.statement.execute(context);
        context.variables().put(name, Wrapper.of(result));
        return result;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("name=");
        stream.print(name);
        stream.print(", ");
        stream.print("value=");
        this.statement.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(name);
        stream.print("=");
        this.statement.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text(name, profile.highlight()),
            Component.text(" = ", profile.pop()),
            this.statement.prettyPrint(profile)
        ).hoverEvent(Component.textOfChildren(Component.text("Sets the variable '" + name + "' to a value."),
            Component.newline(), this.printReturnType(profile)));
    }

    @Override
    public Class<?> returnType() {
        return statement.returnType();
    }

}
