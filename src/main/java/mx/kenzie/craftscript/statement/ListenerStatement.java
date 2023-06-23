package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.emitter.EntityEventListener;
import mx.kenzie.craftscript.emitter.EventListener;
import mx.kenzie.craftscript.emitter.StaticEventListener;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.io.PrintStream;
import java.util.Map;

public record ListenerStatement(Statement<?> key, Statement<?> details, Statement<?> task)
    implements Statement<Object> {

    public static final double DEFAULT_EVENT_RADIUS = 32;

    @Override
    public Object execute(Context context) throws ScriptError {
        final Object a = key.execute(context);
        if (!(Wrapper.unwrap(a) instanceof NamespacedKey key))
            throw new ScriptError("Object '" + a + "' was not an event key.");
        final EventListener.Details data = new EventListener.Details(context.source(), context.data());
        final EventListener listener;
        if (details != null) {
            final Object b = details.execute(context);
            if (!(Wrapper.unwrap(b) instanceof Map<?, ?> map))
                throw new ScriptError("Object '" + b + "' was not an event detail map.");
            final double radius = map.get("radius") instanceof Double d ? d : DEFAULT_EVENT_RADIUS;
            final Object at = map.get("at");
            if (at instanceof Location location)
                listener = new StaticEventListener(data, key, task, radius, location);
            else if (at instanceof CommandSender sender)
                listener = new EntityEventListener(data, key, task, radius, sender);
            else listener = new EntityEventListener(data, key, task, radius, context.source());
        } else listener = new EntityEventListener(data, key, task, DEFAULT_EVENT_RADIUS, context.source());
        context.manager().registerListener(listener);
        return listener;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("key=");
        this.key.debug(stream);
        if (details != null) {
            stream.print(", ");
            stream.print("details=");
            this.details.debug(stream);
        }
        stream.print(", ");
        stream.print("task=");
        this.task.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("on ");
        this.key.stringify(stream);
        if (details != null) this.details.stringify(stream);
        stream.print(' ');
        this.task.stringify(stream);
    }

}
