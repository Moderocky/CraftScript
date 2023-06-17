package mx.kenzie.craftscript.kind;


import mx.kenzie.craftscript.script.Context;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class LocationKind extends Kind<Location> {

    public LocationKind() {
        super(Location.class);
    }

    @Override
    public Object getProperty(Location thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "length" -> thing.length();
            case "x" -> thing.getX();
            case "y" -> thing.getY();
            case "z" -> thing.getZ();
            case "yaw" -> thing.getYaw();
            case "pitch" -> thing.getPitch();
            case "clone" -> thing.clone();
            case "chunk" -> thing.getChunk();
            case "block" -> thing.getBlock();
            case "to_vector" -> thing.toVector();
            default -> null;
        };
    }

    @Override
    public Object setProperty(Location thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "length" -> thing.length() == ((Number) value).doubleValue();
            case "distance" -> thing.distance(((Location) value));
            case "distance_squared" -> thing.distanceSquared(((Location) value));
            default -> null;
        };
    }

    @Override
    public Location fromString(String string) {
        final String[] parts = string.split(" ");
        if (parts.length != 3) return null;
        final World world;
        if (Context.getLocalContext() != null && Context.getLocalContext().source() instanceof Entity entity)
            world = entity.getWorld();
        else world = Bukkit.getWorlds().get(0);
        return new Location(world, Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
            Double.parseDouble(parts[2]));
    }

    @Override
    public String toString(Location location) {
        return location.getX() + " " + location.getY() + " " + location.getZ();
    }

}
