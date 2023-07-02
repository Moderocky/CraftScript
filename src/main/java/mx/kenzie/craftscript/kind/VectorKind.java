package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.script.core.InternalStatement;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

import static mx.kenzie.craftscript.kind.NumberKind.NUMBER;

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
            case "length_squared" -> thing.lengthSquared();
            case "x" -> thing.getX();
            case "y" -> thing.getY();
            case "z" -> thing.getZ();
            case "clone" -> thing.clone();
            case "is_zero" -> thing.isZero();
            case "is_normalized" -> thing.isNormalized();
            case "distance" -> new InternalStatement(arguments -> thing.distance(this.convert(arguments.get(0))));
            case "distance_squared" ->
                new InternalStatement(arguments -> thing.distanceSquared(this.convert(arguments.get(0))));
            case "angle" -> new InternalStatement(arguments -> thing.angle(this.convert(arguments.get(0))));
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "length", "length_squared", "x", "y", "z", "clone", "is_zero", "is_normalized", "distance", "distance_squared", "angle"};
    }

    @Override
    public Object setProperty(Vector thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "x" -> thing.setX(NUMBER.convert(value).doubleValue());
            case "y" -> thing.setY(NUMBER.convert(value).doubleValue());
            case "z" -> thing.setZ(NUMBER.convert(value).doubleValue());
            case "length" -> thing.normalize().multiply(NUMBER.convert(value).doubleValue());
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
