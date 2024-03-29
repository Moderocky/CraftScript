package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.CheckedFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerKind extends CommandSenderKind<Player> {

    public static final PlayerKind PLAYER = new PlayerKind();

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
            case "compass_target" -> thing.getCompassTarget();
            case "can_fly" -> thing.getAllowFlight();
            case "bed_location" -> thing.getBedSpawnLocation();
            case "arrows_in_body" -> thing.getArrowsInBody();
            case "client_brand" -> thing.getClientBrandName();
            case "view_distance" -> thing.getClientViewDistance();
            case "has_cooldown" -> CheckedFunction.of(Kinds.MATERIAL).runs(thing::hasCooldown);
            default -> super.getProperty(thing, property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[] {"type", "name", "uuid", "online", "location", "game_mode", "health", "killer", "last_damage", "yaw", "pitch", "x", "y", "z", "compass_target", "can_fly", "bed_location", "arrows_in_body", "client_brand", "view_distance", "has_cooldown", "name", "type", "has_permission", "send_message", "is_op"};
    }

    @Override
    public Object setProperty(Player thing, String property, Object value) {
        if (thing == null) return false;
        return switch (property) {
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
            default -> super.setProperty(thing, property, value);
        };
    }

    @Override
    public <Theirs> Player convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof String string) return Bukkit.getPlayer(string);
        else if (object instanceof UUID uuid) return Bukkit.getPlayer(uuid);
        return super.convert(object, kind);
    }

    @Override
    public Kind<?> superKind() {
        return COMMAND_SENDER;
    }

}
