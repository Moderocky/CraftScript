package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.PropertyVariableContainer;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record DoStatement(Statement<?> source, Statement<?> then) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        final Context sub = this.prepare(context, Wrapper.of(source.execute(context)));
        try {
            Context.setLocalContext(sub);
            return this.then.execute(sub);
        } finally {
            Context.setLocalContext(context);
        }
    }

    private <Type> Context prepare(Context context, Wrapper<Type> wrapper) {
        final VariableContainer old = context.variables();
        final VariableContainer special = new PropertyVariableContainer<>(old, wrapper);
        final Context child = new Context(context.source(), context.manager(), special, context.data().clone());
        child.data().parentContext = context;
        child.data().script = context.data().script;
        return child;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("source=");
        this.source.debug(stream);
        stream.print(", ");
        stream.print("then=");
        this.then.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("do ");
        this.source.stringify(stream);
        stream.print(' ');
        this.then.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("do ", profile.dark()),
            this.source.prettyPrint(profile),
            Component.space(),
            this.then.prettyPrint(profile)
        ).hoverEvent(Component.text("Creates a block of special actions for this object."));
    }

}
