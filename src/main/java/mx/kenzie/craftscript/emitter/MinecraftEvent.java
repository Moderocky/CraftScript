package mx.kenzie.craftscript.emitter;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.world.GenericGameEvent;

public class MinecraftEvent implements Event {

    protected final GenericGameEvent event;

    public MinecraftEvent(GenericGameEvent event) {
        this.event = event;
    }

    @Override
    public NamespacedKey key() {
        return event.getEvent().getKey();
    }

    @Override
    public Object getProperty(String property) {
        return switch (property) {
            case "location" -> event.getLocation();
            case "entity" -> event.getEntity();
            default -> null;
        };
    }

    @Override
    public Location getLocation() {
        return event.getLocation();
    }

    public int getRadius() {
        return event.getRadius();
    }

}
