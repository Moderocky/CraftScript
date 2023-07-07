package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Container;
import mx.kenzie.craftscript.variable.ContainerMap;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.VariableFinder;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record MapStatement(Statement<?>... statements) implements Statement<Container> {

    @Override
    public Container execute(Context context) throws ScriptError {
        final VariableContainer container = new VariableFinder(context.variables());
        final Context sub = new Context(context.source(), context.manager(), container, context.data());
        for (final Statement<?> statement : statements) statement.execute(sub);
        return new ContainerMap(container);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        ListStatement.debug(stream, statements);
    }

    @Override
    public void stringify(PrintStream stream) {
        ListStatement.stringify(stream, statements);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return ListStatement.prettyPrint(profile, statements)
            .hoverEvent(Component.textOfChildren(Component.text("A key <-> value map of objects.", profile.light()),
                this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Container> returnType() {
        return Container.class;
    }

}
