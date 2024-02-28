package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.emitter.EntityEventListener;
import mx.kenzie.craftscript.emitter.EventListener;
import mx.kenzie.craftscript.emitter.StaticEventListener;
import mx.kenzie.craftscript.script.BukkitScriptManager;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.io.PrintStream;
import java.util.Map;

public record ListenerStatement(Statement<?> key, Statement<?> details, Statement<?> task)
    implements Statement<EventListener> {

    public static final double DEFAULT_EVENT_RADIUS = 32, MAX_EVENT_RADIUS = 128;

    public static EventListener execute(Context<?> context, Object token, Object inputs, Executable<?> task) {
        if (!(Wrapper.unwrap(token) instanceof NamespacedKey key))
            throw new ScriptError("Object '" + token + "' was not an event key.");
        final Context.Data data = context.data().clone();
        data.script = context.getRootContext().data().script;
        final EventListener.Details details = new EventListener.Details(context.getSource(), data);
        final EventListener listener;
        if (inputs != null) {
            if (!(Wrapper.unwrap(inputs) instanceof Map<?, ?> map))
                throw new ScriptError("Object '" + inputs + "' was not an event detail map.");
            final double radius = Math.min(Math.max(0, map.get("radius") instanceof Double d
                ? d
                : DEFAULT_EVENT_RADIUS), MAX_EVENT_RADIUS);
            final Object at = map.get("at");
            if (at instanceof Location location)
                listener = new StaticEventListener(details, key, task, radius, location);
            else if (at instanceof CommandSender sender)
                listener = new EntityEventListener(details, key, task, radius, sender);
            else listener = new EntityEventListener(details, key, task, radius, context.getSource());
        } else listener = new EntityEventListener(details, key, task, DEFAULT_EVENT_RADIUS, context.getSource());
        ((BukkitScriptManager) context.manager()).registerListener(listener);
        return listener;
    }

    @Override
    public EventListener execute(Context context) throws ScriptError {
        return execute(context, key.execute(context), details != null ? details.execute(context) : null, task);
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

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final Component hover = Component.textOfChildren(
            Component.text("Run code when an event occurs.", profile.light()), Component.newline(),
            this.printReturnType(profile));
        if (details == null) return Component.textOfChildren(
            Component.text("on ", profile.dark()),
            this.key.prettyPrint(profile),
            Component.space(),
            this.task.prettyPrint(profile)
        ).hoverEvent(hover);
        else return Component.textOfChildren(
            Component.text("on ", profile.dark()),
            this.key.prettyPrint(profile),
            this.details.prettyPrint(profile),
            Component.space(),
            this.task.prettyPrint(profile)
        ).hoverEvent(hover);
    }

    @Override
    public Class<? extends EventListener> returnType() {
        return EventListener.class;
    }

}
