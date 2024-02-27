package mx.kenzie.craftscript.kind;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;

public class BlockKind extends Kind<Block> {

    public static final BlockKind BLOCK = new BlockKind();

    public BlockKind() {
        super(Block.class);
    }

    @Override
    public Object getProperty(Block thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "material" -> thing.getType();
            case "is_empty" -> thing.isEmpty();
            case "is_powered" -> thing.isBlockPowered();
            case "is_passable" -> thing.isPassable();
            case "x" -> thing.getX();
            case "y" -> thing.getY();
            case "z" -> thing.getZ();
            case "entity" -> thing.getState() instanceof TileState tile ? tile : null;
            case "state" -> thing.getBlockData();
            case "location" -> thing.getLocation();
            default -> BlockDataKind.BLOCK_DATA.getProperty(thing.getBlockData(), property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[] {"type", "material", "is_empty", "is_powered", "is_passable", "x", "y", "z", "entity", "state", "location"};
    }

    @Override
    public <Theirs> Block convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Location location) return location.getBlock();
        if (object instanceof Entity entity) return entity.getLocation().getBlock();
        return super.convert(object, kind);
    }

    @Override
    public String toString(Block block) {
        return block.getBlockData().getAsString(true);
    }

}
