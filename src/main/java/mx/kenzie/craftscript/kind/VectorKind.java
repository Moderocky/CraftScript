package mx.kenzie.craftscript.kind;

import org.bukkit.util.Vector;

public class VectorKind extends Kind<Vector> {

    public VectorKind() {
        super(Vector.class);
    }

    @Override
    public Object getProperty(Vector thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "length" -> thing.length();
            case "x" -> thing.getX();
            case "y" -> thing.getY();
            case "z" -> thing.getZ();
            case "clone" -> thing.clone();
            case "is_zero" -> thing.isZero();
            case "is_normalized" -> thing.isNormalized();
            default -> null;
        };
    }

    @Override
    public Object setProperty(Vector thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "length" -> thing.length() == ((Number) value).doubleValue();
            case "distance" -> thing.distance(((Vector) value));
            case "distance_squared" -> thing.distanceSquared(((Vector) value));
            default -> null;
        };
    }

    @Override
    public String toString(Vector location) {
        return location.getX() + " " + location.getY() + " " + location.getZ();
    }

}
