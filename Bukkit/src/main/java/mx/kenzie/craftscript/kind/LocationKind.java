package mx.kenzie.craftscript.kind;


import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.core.CheckedFunction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class LocationKind extends Kind<Location> {

    public static final LocationKind LOCATION = new LocationKind();

    public LocationKind() {
        super(Location.class);
    }

    @Override
    public Object getProperty(Location thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "length" -> thing.length();
            case "length_squared" -> thing.lengthSquared();
            case "x" -> thing.getX();
            case "y" -> thing.getY();
            case "z" -> thing.getZ();
            case "yaw" -> thing.getYaw();
            case "pitch" -> thing.getPitch();
            case "clone" -> thing.clone();
            case "chunk" -> thing.getChunk();
            case "block" -> thing.getBlock();
            case "is_loaded" -> thing.isChunkLoaded();
            case "is_generated" -> thing.isGenerated();
            case "to_vector" -> thing.toVector();
            case "direction" -> thing.getDirection();
            case "distance" -> CheckedFunction.of(this).runs(thing::distance);
            case "distance_squared" -> CheckedFunction.of(this).runs(thing::distanceSquared);
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[] {"type", "length", "length_squared", "x", "y", "z", "yaw", "pitch", "clone", "chunk", "block", "is_loaded", "is_generated", "to_vector", "direction", "distance", "distance_squared"};
    }

    @Override
    public <Theirs> Location convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Vector vector && Context.requireLocalContext().source() instanceof Entity entity)
            return vector.toLocation(entity.getWorld());
        if (object instanceof Entity entity) return entity.getLocation();
        if (object instanceof Block block) return block.getLocation();
        if (object instanceof BlockState state) return state.getLocation();
        return super.convert(object, kind);
    }

    @Override
    public String toString(Location location) {
        return location.getX() + " " + location.getY() + " " + location.getZ();
    }

}
