package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;
import java.util.*;

public record RunStatement(Statement<?> statement, Statement<?> data) implements Statement<Object> {

    static Object run(Executable<?> executable, Context context, VariableContainer container) {
        final Context sub = new Context(context.source(), context.manager(), container, context.data().clone());
        sub.data().parentContext = context;
        Context.setLocalContext(sub);
        try {
            return executable.execute(sub);
        } finally {
            Context.setLocalContext(context);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(Context context) throws ScriptError {
        final Object result = Wrapper.unwrap(statement.execute(context));
        VariableContainer variables = new VariableContainer();
        if (data != null) {
            final Object found = Wrapper.unwrap(data.execute(context));
            if (found instanceof VariableContainer container) {
                variables = container;
//            }
//            if (found != null && found.getClass() == VariableContainer.class) {
//                variables = (VariableContainer) found; // special reflective case!
            } else if (found instanceof Map<?, ?> map) {
                variables.putAll((Map<String, Object>) map);
                variables.put("$parameters", new ArrayList<>(map.values()));
            } else if (found instanceof Collection<?> collection) {
                variables.put("$parameters", new ArrayList<>(collection));
            } else if (found instanceof Object[] objects) {
                variables.put("$parameters", Arrays.asList(objects));
            } else variables.put("$parameters", Collections.singletonList(found));
        } else variables.put("$parameters", Collections.emptyList());
        if (result instanceof Executable<?> executable) return run(executable, context, variables);
        else if (result instanceof Runnable runnable) {
            runnable.run();
            return null;
        } else throw new ScriptError("Unable to run object '" + result + "'.");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        stream.print("statement=");
        this.statement.debug(stream);
        if (data != null) {
            stream.print(", ");
            stream.print("data=");
            this.data.debug(stream);
        }
        stream.print(']');
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
            Component.text("run ", profile.dark()),
            this.statement.prettyPrint(profile)
        ).hoverEvent(Component.text("Run an executable task.", profile.light()));
        else return Component.textOfChildren(
            Component.text("run ", profile.dark()),
            this.statement.prettyPrint(profile),
            Component.space(),
            this.data.prettyPrint(profile)
        ).hoverEvent(Component.text("Run an executable task with inputs.", profile.light()));
    }

}
