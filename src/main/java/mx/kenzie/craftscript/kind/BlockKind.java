package mx.kenzie.craftscript.kind;

import org.bukkit.block.Block;

public class BlockKind extends Kind<Block> {

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
            case "state" -> thing.getState();
            case "data" -> thing.getBlockData();
            default -> null;
        };
    }

    @Override
    public Block fromString(String string) {
        return null;
    }

    @Override
    public String toString(Block block) {
        return block.getBlockData().getAsString(true);
    }

}
