package mx.kenzie.craftscript.emitter;

import mx.kenzie.craftscript.utility.Executable;
import org.bukkit.Location;

/**
 * An event listener at a static location.
 */
public class StaticEventListener extends EventListener {

    protected final Location location;

    protected StaticEventListener(Details details, Executable<?> trigger, double radius, Location location) {
        super(details, trigger, radius);
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
