package mx.kenzie.craftscript.emitter;

import org.bukkit.NamespacedKey;

public interface Event {

    NamespacedKey key();

    Object getProperty(String property);

}
