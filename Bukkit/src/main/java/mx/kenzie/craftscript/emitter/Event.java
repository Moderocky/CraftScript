package mx.kenzie.craftscript.emitter;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;

public interface Event {

    NamespacedKey key();

    Object getProperty(String property);

    default Location getLocation() {
        return (Location) this.getProperty("location");
    }

}
