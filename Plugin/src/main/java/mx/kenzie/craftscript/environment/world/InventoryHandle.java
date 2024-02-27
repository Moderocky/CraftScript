package mx.kenzie.craftscript.environment.world;

import mx.kenzie.craftscript.environment.kind.ItemHandleKind;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public record InventoryHandle(Inventory inventory, boolean readOnly) implements Handle {

    public boolean contains(ItemQuery query) {
        for (final ItemStack item : inventory) if (query.matches(item)) return true;
        return false;
    }

    public Wrapper<ItemHandle> remove(ItemQuery query) {
        if (readOnly) throw new ScriptError("Attempted to take from a read-only inventory.");
        for (final ItemStack item : inventory)
            if (query.matches(item)) return new Wrapper<>(new ItemHandle(query.subtract(item), false),
                new ItemHandleKind());
        return null;
    }

    public boolean add(ItemHandle handle) {
        if (handle == null) return true;
        if (readOnly) throw new ScriptError("Attempted to add to a read-only inventory.");
        if (handle.readOnly()) throw new ScriptError("Attempted to add a read-only item to an inventory.");
        final Map<Integer, ItemStack> map = this.inventory.addItem(handle.item);
        if (map.isEmpty()) {
            handle.item = null;
            handle.readOnly = true;
            return true;
        } else {
            handle.item = map.values().iterator().next();
            handle.readOnly = false;
            return false;
        }
    }

    @Override
    public String toString() {
        if (inventory == null) return "null";
        return "<inventory>";
    }

}
