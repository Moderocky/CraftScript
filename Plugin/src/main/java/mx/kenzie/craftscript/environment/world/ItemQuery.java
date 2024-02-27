package mx.kenzie.craftscript.environment.world;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemQuery {

    protected final Material material;
    protected final int count;
    protected final Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemQuery(Material material, int count) {
        this.material = material;
        this.count = count;
    }

    public ItemQuery(NamespacedKey key) {
        this.material = Material.matchMaterial(key.asString(), false);
        this.count = 1;
    }

    public ItemQuery(ItemStack item) {
        if (item == null) throw new ScriptError("Item is empty.");
        this.material = item.getType();
        this.count = item.getAmount();
        this.enchantments.putAll(item.getEnchantments());
    }

    private static <Key, Value> boolean containsAll(Map<Key, Value> source, Map<Key, Value> answer) {
        return source.entrySet().containsAll(answer.entrySet());
    }

    public static ItemQuery of(Object thing) {
        final Object object = Wrapper.unwrap(thing);
        if (object instanceof ItemQuery query) return query;
        if (object instanceof ItemStack item) return new ItemQuery(item);
        if (object instanceof NamespacedKey key) return new ItemQuery(key);
        if (object instanceof Material material) return new ItemQuery(material, 1);
        throw new ScriptError("Unable to turn '" + thing + "' into an item query.");
    }

    public boolean matchesExactly(ItemStack item) {
        return item.getType() == material
            && item.getAmount() == count
            && item.getEnchantments().entrySet().equals(enchantments.entrySet());
    }

    public boolean matches(ItemStack item) {
        if (item == null) return false;
        return item.getType() == material
            && item.getAmount() >= count
            && containsAll(item.getEnchantments(), enchantments);
    }

    public ItemStack subtract(ItemStack item) {
        if (item == null) return null;
        final ItemStack copy = item.clone();
        final int remainder = item.getAmount() - count, taken;
        if (remainder < 0) taken = count + remainder;
        else taken = count;
        copy.setAmount(taken);
        item.setAmount(Math.max(0, remainder));
        return copy;
    }

}
