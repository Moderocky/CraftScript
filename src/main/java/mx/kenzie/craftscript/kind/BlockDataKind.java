package mx.kenzie.craftscript.kind;


import org.bukkit.block.data.BlockData;

public class BlockDataKind extends Kind<BlockData> {

    public BlockDataKind() {
        super(BlockData.class);
    }

    @Override
    public Object getProperty(BlockData thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this;
            case "material" -> thing.getMaterial();
            case "is_occluding" -> thing.isOccluding();
            case "light" -> thing.getLightEmission();
            case "placement_material" -> thing.getPlacementMaterial();
            case "requires_tool" -> thing.requiresCorrectToolForDrops();
            default -> null;
        };
    }

    @Override
    public String toString(BlockData data) {
        return data.getAsString(true);
    }

}
