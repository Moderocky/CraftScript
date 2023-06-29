package mx.kenzie.craftscript.emitter;

import org.bukkit.event.world.GenericGameEvent;

public class MinecraftEvent extends SimpleEvent {

    protected final GenericGameEvent event;

    public MinecraftEvent(GenericGameEvent event) {
        super(event.getEntity(), event.getLocation(), event.getEvent().getKey());
        this.event = event;
    }

    public int getRadius() {
        return event.getRadius();
    }

}
