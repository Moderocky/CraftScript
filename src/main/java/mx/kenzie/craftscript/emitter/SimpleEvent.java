package mx.kenzie.craftscript.emitter;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

public class SimpleEvent implements Event {

    protected final Entity entity;
    protected final Location location;
    protected final NamespacedKey key;

    public SimpleEvent(Entity entity, Location location, NamespacedKey key) {
        this.entity = entity;
        this.location = location;
        this.key = key;
    }

    @Override
    public NamespacedKey key() {
        return key;
    }

    @Override
    public Object getProperty(String property) {
        return switch (property) {
            case "location" -> location;
            case "entity" -> entity;
            default -> null;
        };
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
