package mx.kenzie.craftscript.kind;


import org.bukkit.Location;

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
    public String toString(Location location) {
        return location.getX() + " " + location.getY() + " " + location.getZ();
    }

}
