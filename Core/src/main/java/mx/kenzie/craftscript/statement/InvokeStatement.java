package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.core.SuperFunction;
import mx.kenzie.craftscript.variable.VariableContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import java.io.PrintStream;

public record InvokeStatement<Result>(String name, Statement<?> source, Statement<?>... arguments)
    implements Statement<Object>, VariableReferenceStatement {

    public static <Result, Source> Result execute(Context<Source> context, SuperFunction<Result> executable, SuperFunction.Arguments arguments) {
        final VariableContainer container = new VariableContainer();
        final Context<?> sub = new Context<>(context.source(), context.manager(), container, context.data().clone());
        sub.data().parentContext = context;
        Context.setLocalContext(sub);
        try {
            executable.prepare(sub, arguments);
            return executable.execute(sub);
        } finally {
            Context.setLocalContext(context);
        }
    }

    @Override
    public Result execute(Context<?> context) throws ScriptError {
        final SuperFunction<Result> function = SuperFunction.of(source.execute(context));
        return InvokeStatement.execute(context, function, this.getArguments(context, arguments));
    }

    public SuperFunction.Arguments getArguments(Context<?> context, Statement<?>... statements) {
        final SuperFunction.Argument[] inputs = new SuperFunction.Argument[statements.length];
        for (int i = 0; i < statements.length; i++) {
            final Statement<?> statement = statements[i];
            if (statement instanceof VariableReferenceStatement reference) {
                inputs[i] = new SuperFunction.Argument(reference.name(), reference.execute(context));
            } else inputs[i] = new SuperFunction.Argument(null, statement.execute(context));
        }
        return new SuperFunction.Arguments(inputs);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        stream.print("source=");
        this.source.debug(stream);
        stream.print(',');
        stream.println("arguments=[");
        boolean first = true;
        for (Statement<?> statement : arguments) {
            if (first) first = false;
            else stream.print(',');
            statement.debug(stream);
        }
        stream.println(']');
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        source.stringify(stream);
        stream.print('(');
        boolean first = true;
        for (Statement<?> statement : arguments) {
            if (first) first = false;
            else stream.print(", ");
            statement.stringify(stream);
        }
        stream.print(')');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            this.source.prettyPrint(profile),
            Component.text("(", profile.pop()),
            Component.join(JoinConfiguration.separator(Component.text(", ", profile.pop())), arguments),
            Component.text(")", profile.pop())
        ).hoverEvent(Component.textOfChildren(Component.text("Creates an executable function object.", profile.light()),
            Component.newline(), this.printReturnType(profile)));
    }

    @Override
    public Class<?> returnType() {
        return Object.class;
    }

}
