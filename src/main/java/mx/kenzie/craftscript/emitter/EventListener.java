package mx.kenzie.craftscript.emitter;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import java.util.Objects;

/**
 * Event listeners have relevancy.
 * We don't care about things that happen miles away.
 */
public abstract class EventListener implements Executable<Object> {

    protected final Details details;
    protected final NamespacedKey key;
    protected final Executable<?> trigger;
    protected final double radius;

    protected EventListener(Details details, NamespacedKey key, Executable<?> trigger, double radius) {
        this.details = details;
        this.key = key;
        this.trigger = trigger;
        this.radius = radius;
    }

    public NamespacedKey event() {
        return key;
    }

    public boolean isRelevant(Event event) {
        if (!Objects.equals(key, event.key())) return false;
        final double radius = this.radius(), distance = radius * radius;
        if (event.getLocation() == null) return true;
        return this.getLocation().distanceSquared(event.getLocation()) < distance;
    }


    public double radius() {
        return radius;
    }

    public Details getDetails() {
        return details;
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        return trigger.execute(context);
    }

    public abstract Location getLocation();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof EventListener listener)) return false;
        return Objects.equals(key, listener.key)
            && trigger == listener.trigger && details.data.script == listener.details.data.script;
    }

    @Override
    public int hashCode() {
        return Objects.hash(details, key, radius);
    }

    public record Details(CommandSender owner, Context.Data data) {}

}
