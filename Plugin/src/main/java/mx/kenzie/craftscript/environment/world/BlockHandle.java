package mx.kenzie.craftscript.environment.world;

import mx.kenzie.craftscript.environment.kind.InventoryHandleKind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.variable.Wrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;

public record BlockHandle(Block block, boolean readOnly) implements Handle {

    public Location getLocation() {
        return block.getLocation();
    }

    public BlockData getData() {
        return block.getBlockData();
    }

    public Material getType() {
        return block.getType();
    }

    public Wrapper<InventoryHandle> getInventory() {
        final Context context = Context.requireLocalContext();
        final BlockState state = (BlockState) context.manager().executeOnPrimary(context, block::getState);
        if (state instanceof BlockInventoryHolder holder) {
            final Inventory inventory = holder.getInventory();
            return new Wrapper<>(new InventoryHandle(inventory, readOnly), new InventoryHandleKind());
        }
        return null;
    }

    @Override
    public String toString() {
        if (block == null) return "null";
        return block.getType().getKey() + "[x=" + block.getX() + ",y=" + block.getY() + ",z=" + block.getZ() + ']';
    }

}
