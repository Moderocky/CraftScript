package mx.kenzie.craftscript.emitter;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * Event listeners have relevancy.
 * We don't care about things that happen miles away.
 */
public abstract class EventListener implements Executable<Object> {

    protected final Details details;
    protected final Executable<?> trigger;
    protected final double radius;

    protected EventListener(Details details, Executable<?> trigger, double radius) {
        this.details = details;
        this.trigger = trigger;
        this.radius = radius;
    }

    public boolean isRelevant(Event event) {
        final double radius = this.radius(), distance = radius * radius;
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

    public record Details(CommandSender owner, Script source, int line) {}

}
