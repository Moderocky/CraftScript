package mx.kenzie.craftscript.kind;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockDataKind extends Kind<BlockData> {

    public static final BlockDataKind BLOCK_DATA = new BlockDataKind();

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
            case "piston_reaction" -> thing.getPistonMoveReaction();
            case "placement_material" -> thing.getPlacementMaterial();
            case "requires_tool" -> thing.requiresCorrectToolForDrops();
            case "light_emission" -> thing.getLightEmission();
            case "clone" -> thing.clone();
            default -> MaterialKind.MATERIAL.getProperty(thing.getMaterial(), property);
        };
    }

    @Override
    public <Theirs> BlockData convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Material material) return material.createBlockData();
        if (object instanceof String string) return Bukkit.createBlockData(string);
        if (object instanceof Block block) return block.getBlockData();
        return super.convert(object, kind);
    }

    @Override
    public String[] getProperties() {
        return new String[]{"type", "material", "is_occluding", "piston_reaction", "placement_material", "requires_tool", "light_emission", "clone"};
    }

    @Override
    public String toString(BlockData data) {
        return data.getAsString(true);
    }

    @Override
    public String toString() {
        return "#blockstate";
    }

}
