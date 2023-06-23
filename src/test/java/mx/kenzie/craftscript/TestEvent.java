package mx.kenzie.craftscript;

import mx.kenzie.craftscript.emitter.Event;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

public class TestEvent implements Event {

    @Override
    public NamespacedKey key() {
        return new NamespacedKey("test", "event");
    }

    @Override
    public Object getProperty(String property) {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

}
