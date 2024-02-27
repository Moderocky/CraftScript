package mx.kenzie.craftscript.environment.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.environment.world.ItemQuery;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.io.PrintStream;

public record ItemQueryStatement(String query, ItemStack item) implements Statement<ItemQuery> {

    @Override
    public ItemQuery execute(Context context) throws ScriptError {
        return new ItemQuery(item);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("item=");
        stream.print(item);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(query);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text(query, profile.light())
            .hoverEvent(Component.text("An item matcher.", profile.light()));
    }

    @Override
    public Class<? extends ItemQuery> returnType() {
        return ItemQuery.class;
    }

}
