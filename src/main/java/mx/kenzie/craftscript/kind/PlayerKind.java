package mx.kenzie.craftscript.kind;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerKind extends Kind<Player> {

    public PlayerKind() {
        super(Player.class);
    }

    @Override
    public Object getProperty(Player thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "name" -> thing.getName();
            case "uuid" -> thing.getUniqueId();
            case "online" -> thing.isOnline();
            case "location" -> thing.getLocation();
            case "game_mode" -> thing.getGameMode();
            case "health" -> thing.getHealth();
            case "killer" -> thing.getKiller();
            case "last_damage" -> thing.getLastDamage();
            case "yaw" -> thing.getLocation().getYaw();
            case "pitch" -> thing.getLocation().getPitch();
            case "x" -> thing.getLocation().getX();
            case "y" -> thing.getLocation().getY();
            case "z" -> thing.getLocation().getZ();
            case "compass" -> thing.getCompassTarget();
            case "can_fly" -> thing.getAllowFlight();
            case "bed_location" -> thing.getBedSpawnLocation();
            case "arrows_in_body" -> thing.getArrowsInBody();
            case "client_brand" -> thing.getClientBrandName();
            case "view_distance" -> thing.getClientViewDistance();
            default -> null;
        };
    }

    @Override
    public Object setProperty(Player thing, String property, Object value) {
        if (thing == null) return false;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "killer" -> {
                thing.setKiller((Player) value);
                yield value;
            }
            case "last_damage" -> {
                thing.setLastDamage((Double) value);
                yield value;
            }
            case "yaw" -> {
                thing.setRotation((Float) value, thing.getLocation().getPitch());
                yield value;
            }
            case "pitch" -> {
                thing.setRotation(thing.getLocation().getYaw(), (Float) value);
                yield value;
            }
            case "has_permission" -> thing.hasPermission((String) value);
            case "has_cooldown" -> thing.hasCooldown((Material) value);
            default -> null;
        };
    }

    @Override
    public Player fromString(String string) {
        return Bukkit.getPlayer(string);
    }

    @Override
    public String toString(Player player) {
        return player.getName();
    }

}
