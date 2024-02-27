package mx.kenzie.craftscript.environment.parser;

import mx.kenzie.craftscript.environment.statement.ItemQueryStatement;
import mx.kenzie.craftscript.parser.BasicParser;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static mx.kenzie.craftscript.parser.ResourceParser.isValidKey;
import static mx.kenzie.craftscript.parser.ResourceParser.isValidNamespace;

public class ItemQueryParser extends BasicParser {

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        if (!input.endsWith("}")) return false;
        if (!input.contains("{")) return false;
        final int brace = input.indexOf('{'), colon;
        if (brace < 1) return false;
        final String before;
        before = input.substring(0, brace).trim();
        if (before.isEmpty()) return false;
        colon = before.indexOf(':');
        if (colon > 0) {
            final String[] parts;
            parts = input.split(":", 2);
            if (!(parts.length == 2 && isValidNamespace(parts[0]) && isValidKey(parts[1]))) return false;
        } else if (!isValidKey(before)) return false;
        final Material material = Material.matchMaterial(before, false);
        return (material != null);
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        if (Bukkit.getServer() == null) return new ItemQueryStatement(input, new ItemStack(Material.STONE));
        try {
            final ItemStack item = Bukkit.getItemFactory().createItemStack(input);
            return new ItemQueryStatement(input, item);
        } catch (IllegalArgumentException ex) {
            throw new ScriptError("Item query '" + input + "' contained badly-formed item NBT.", ex);
        } catch (Exception ex) {
            throw new ScriptError("Unknown error parsing item query '" + input + "'.", ex);
        }
    }

    @Override
    public void close() throws ScriptError {
        super.close();
    }

}
