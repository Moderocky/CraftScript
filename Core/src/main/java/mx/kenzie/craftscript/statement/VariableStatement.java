package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.VariableHelper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record VariableStatement(String name) implements EvaluatedStatement<Object>, VariableReferenceStatement {

    @Override
    public Object execute(Context<?> context) throws ScriptError {
        return context.variables().get(name);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("name=");
        stream.print(name);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(name);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text(name, profile.highlight())
            .hoverEvent(Component.textOfChildren(Component.text("The '" + name + "' variable.", profile.light()),
                Component.newline(), this.printReturnType(profile)));
    }

    @Override
    public Class<?> returnType() {
        return VariableHelper.instance().getReturnType(name);
    }

    @Override
    public Class<?> evaluatedReturnType() {
        final Statement<?> statement = VariableHelper.instance().getAssignment(name);
        if (statement == null) return Object.class;
        return statement instanceof EvaluatedStatement<?> evaluated ? evaluated.evaluatedReturnType() : statement.returnType();
    }

}
