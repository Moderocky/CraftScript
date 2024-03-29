package mx.kenzie.craftscript.listener;

import mx.kenzie.craftscript.emitter.MinecraftEvent;
import mx.kenzie.craftscript.script.BukkitScriptManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;

public class GameEventListener implements Listener {

    protected final BukkitScriptManager manager;

    public GameEventListener(BukkitScriptManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEvent(GenericGameEvent event) {
        if (event.isCancelled()) return;
        this.manager.emit(new MinecraftEvent(event));
    }

}
