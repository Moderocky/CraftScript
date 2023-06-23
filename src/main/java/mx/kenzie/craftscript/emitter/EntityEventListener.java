package mx.kenzie.craftscript.emitter;

import mx.kenzie.craftscript.utility.Executable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * An event listener that follows an entity (and lingers where they expire).
 */
public class EntityEventListener extends EventListener {

    protected final Reference<CommandSender> reference;
    protected transient Location previous;

    protected EntityEventListener(Details details, Executable<?> trigger, double radius, CommandSender source) {
        super(details, trigger, radius);
        this.reference = new WeakReference<>(source);
        this.previous = source instanceof Entity entity
            ? entity.getLocation()
            : Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    @Override
    public Location getLocation() {
        if (reference.get() instanceof Entity entity) return previous = entity.getLocation();
        return previous;
    }

}
