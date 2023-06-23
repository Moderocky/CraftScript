package mx.kenzie.craftscript.emitter;

import mx.kenzie.craftscript.utility.Executable;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

/**
 * An event listener at a static location.
 */
public class StaticEventListener extends EventListener {

    protected final Location location;

    public StaticEventListener(Details details, NamespacedKey key, Executable<?> trigger, double radius, Location location) {
        super(details, key, trigger, radius);
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
