package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.PropertyVariableContainer;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record DoStatement(Statement<?> source, Statement<?> then) implements Statement<Object> {

    public static Object execute(Context<?> context, Object source, Executable<?> then) {
        final Context<?> sub = prepare(context, Wrapper.of(source));
        try {
            Context.setLocalContext(sub);
            return then.execute(sub);
        } finally {
            Context.setLocalContext(context);
        }
    }

    private static <Type> Context<?> prepare(Context<?> parent, Wrapper<Type> wrapper) {
        final VariableContainer old = parent.variables();
        final VariableContainer special = new PropertyVariableContainer<>(old, wrapper);
        final Context<?> context = new Context(parent.source(), parent.manager(), special, parent.data().clone());
        context.data().parentContext = parent;
        context.data().script = parent.data().script;
        wrapper.getKind().setupDoBlock(context);
        return context;
    }

    @Override
    public Object execute(Context<?> context) throws ScriptError {
        return execute(context, source.execute(context), then);
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
        ).hoverEvent(Component.textOfChildren(
            Component.text("Creates a block of special actions for this object.", profile.light()),
            Component.newline(),
            this.printReturnType(profile)));
    }

    @Override
    public Class<?> returnType() {
        return then.returnType();
    }

}
