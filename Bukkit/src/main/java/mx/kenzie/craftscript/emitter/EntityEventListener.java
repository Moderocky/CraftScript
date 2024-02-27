package mx.kenzie.craftscript.emitter;

import mx.kenzie.craftscript.utility.Executable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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

    public EntityEventListener(Details details, NamespacedKey key, Executable<?> trigger, double radius, CommandSender source) {
        super(details, key, trigger, radius);
        this.reference = new WeakReference<>(source);
        this.previous = source instanceof Entity entity
            ? entity.getLocation()
            : Bukkit.getServer() != null
            ? Bukkit.getWorlds().get(0).getSpawnLocation()
            : new Location(null, 0, 0, 0);
    }

    @Override
    public Location getLocation() {
        if (reference.get() instanceof Entity entity) return previous = entity.getLocation();
        return previous;
    }

}
