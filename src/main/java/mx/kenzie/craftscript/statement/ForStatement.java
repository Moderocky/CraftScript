package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.stream.Stream;

public record ForStatement(VariableAssignmentStatement assignment, Statement<?> then) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final Statement<?> fetch = assignment.statement();
        final Object values = Wrapper.unwrap(fetch.execute(context));
        if (values instanceof Iterable<?> iterable) for (final Object value : iterable) this.trip(context, value);
        else if (values instanceof Iterator<?> iterator) while (iterator.hasNext()) this.trip(context, iterator.next());
        else if (values instanceof Stream<?> stream) stream.forEach(object -> this.tripAsync(context, object));
        else if (values instanceof Object[] objects) for (final Object value : objects) this.trip(context, value);
        else if (values == null) return false;
        else if (values.getClass().isArray()) {
            final int length = Array.getLength(values);
            for (int i = 0; i < length; i++) this.trip(context, Array.get(values, i));
        } else this.trip(context, values);
        return true;
    }

    private void trip(Context context, Object value) {
        context.variables().put(assignment.name(), Wrapper.of(value));
        this.then.execute(context);
    }

    private void tripAsync(Context context, Object value) {
        final Context old = Context.getLocalContext();
        try {
            Context.setLocalContext(context);
            this.trip(context, value);
        } finally {
            Context.setLocalContext(old);
        }
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("assignment=");
        this.assignment.debug(stream);
        stream.print(", ");
        stream.print("then=");
        this.then.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("for ");
        this.assignment.stringify(stream);
        stream.print(' ');
        this.then.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("for ", profile.dark()),
            this.assignment.prettyPrint(profile),
            Component.space(),
            this.then.prettyPrint(profile)
        ).hoverEvent(
            Component.textOfChildren(Component.text("Runs a function for every element in a list.", profile.light()),
                this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Boolean> returnType() {
        return Boolean.class;
    }

}
