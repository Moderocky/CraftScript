package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.VariableHelper;
import mx.kenzie.craftscript.variable.PropertyVariableContainer;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record LocalKeywordStatement(String name) implements Statement<Object>, EvaluatedStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        if (context.variables() instanceof PropertyVariableContainer<?> container)
            return container.getProperty(name);
        else return context.variables().get(name);
    }

    public static Object execute(Context context, String name) {
        if (context.variables() instanceof PropertyVariableContainer<?> container)
            return container.getProperty(name);
        else return context.variables().get(name);
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
            .hoverEvent(
                Component.textOfChildren(Component.text("The '" + name + " local keyword property.", profile.light()),
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
