package mx.kenzie.craftscript.kind;


import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public class MaterialKind extends EnumKind<Material> {

    public static final MaterialKind MATERIAL = new MaterialKind();

    public MaterialKind() {
        super(Material.class);
    }

    @Override
    public Object getProperty(Enum<Material> thing, String property) {
        if (thing instanceof Material material) return this.getProperty(material, property);
        return super.getProperty(thing, property);
    }

    public Object getProperty(Material thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "is_occluding" -> thing.isOccluding();
            case "is_air" -> thing.isAir();
            case "is_empty" -> thing.isEmpty();
            case "is_block" -> thing.isBlock();
            case "is_burnable" -> thing.isBurnable();
            case "is_collidable" -> thing.isCollidable();
            case "is_fuel" -> thing.isFuel();
            case "is_flammable" -> thing.isFlammable();
            case "is_interactive" -> thing.isInteractable();
            case "is_edible" -> thing.isEdible();
            case "is_item" -> thing.isItem();
            case "is_record" -> thing.isRecord();
            case "is_solid" -> thing.isSolid();
            case "equipment_slot" -> thing.getEquipmentSlot();
            case "max_stack_size" -> thing.getMaxStackSize();
            case "max_durability" -> (int) thing.getMaxDurability();
            case "block_data" -> thing.createBlockData();
            case "blast_resistance" -> (double) thing.getBlastResistance();
            case "hardness" -> (double) thing.getHardness();
            case "slipperiness" -> (double) thing.getSlipperiness();
            case "rarity" -> thing.getItemRarity();
            case "category" -> thing.getCreativeCategory();
            case "gravity" -> thing.hasGravity();
            case "key" -> thing.getKey();
            case "id" -> thing.ordinal();
            default -> super.getProperty(thing, property);
        };
    }

    @Override
    public <Theirs> Enum<Material> convert(Theirs object, Kind<? super Theirs> kind) {
        if (object instanceof Material material) return material;
        if (object instanceof String string) return Material.matchMaterial(string, false);
        if (object instanceof NamespacedKey key) return Material.matchMaterial(key.getKey(), false);
        if (object instanceof BlockData data) return data.getMaterial();
        if (object instanceof Block block) return block.getType();
        if (object instanceof ItemStack item) return item.getType();
        return super.convert(object, kind);
    }

    public String toString(Material data) {
        return data.getKey().toString();
    }

    @Override
    public String toString(Enum<Material> materialEnum) {
        if (materialEnum instanceof Material material) return this.toString(material);
        return super.toString(materialEnum);
    }

}
