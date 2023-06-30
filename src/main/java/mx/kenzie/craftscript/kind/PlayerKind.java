package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.InternalStatement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerKind extends CommandSenderKind {

    public static final PlayerKind PLAYER = new PlayerKind();

    public PlayerKind() {
        super(Player.class);
    }

    @Override
    public Object getProperty(CommandSender thing, String property) {
        if (thing instanceof Player player) return this.getProperty(player, property);
        return super.getProperty(thing, property);
    }

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
            case "has_cooldown" -> new InternalStatement(
                (context, arguments) -> thing.hasCooldown((Material) MaterialKind.MATERIAL.convert(arguments.get(0))));
            default -> super.getProperty(thing, property);
        };
    }

    @Override
    public Object setProperty(CommandSender thing, String property, Object value) {
        if (thing instanceof Player player) return this.setProperty(player, property, value);
        return super.setProperty(thing, property, value);
    }

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
            case "has_cooldown" -> thing.hasCooldown((Material) value);
            default -> super.setProperty(thing, property, value);
        };
    }

    @Override
    public <Theirs> Player convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof String string) return Bukkit.getPlayer(string);
        else if (object instanceof UUID uuid) return Bukkit.getPlayer(uuid);
        return (Player) super.convert(object, kind);
    }

}
