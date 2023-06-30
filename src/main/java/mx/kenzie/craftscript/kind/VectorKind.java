package mx.kenzie.craftscript.kind;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public class VectorKind extends Kind<Vector> {

    public static final VectorKind VECTOR = new VectorKind();

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
    public <Theirs> Vector convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Location location) return location.toVector();
        if (object instanceof List<?> arguments && arguments.size() == 3
            && arguments.get(0) instanceof Number x && arguments.get(1) instanceof Number y
            && arguments.get(2) instanceof Number z)
            return new Vector(x.doubleValue(), y.doubleValue(), z.doubleValue());
        return super.convert(object, kind);
    }

    @Override
    public String toString(Vector location) {
        return location.getX() + " " + location.getY() + " " + location.getZ();
    }

}
