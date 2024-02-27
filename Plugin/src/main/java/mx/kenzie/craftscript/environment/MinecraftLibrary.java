package mx.kenzie.craftscript.environment;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.CraftScriptPlugin;
import mx.kenzie.craftscript.environment.kind.BlockHandleKind;
import mx.kenzie.craftscript.environment.kind.InventoryHandleKind;
import mx.kenzie.craftscript.environment.world.BlockHandle;
import mx.kenzie.craftscript.environment.world.InventoryHandle;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.core.InternalStatement;
import mx.kenzie.craftscript.script.core.SupplierStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.variable.LibraryObject;
import mx.kenzie.craftscript.variable.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

public class MinecraftLibrary extends LibraryObject {

    private static final Statement<?> TIME = new SupplierStatement(MinecraftLibrary::time);
    private static final Statement<?> WAIT = new InternalStatement((NumberFunction) number -> {
        final Context context = Context.getLocalContext();
        if (context == null) return false;
        try {
            final int ticks = number.intValue();
            CraftScriptPlugin.scripts.pauseCheckIn(context);
            final long end = System.currentTimeMillis() + (ticks * 50L);
            LockSupport.parkUntil(end);
        } finally {
            CraftScriptPlugin.scripts.checkIn(context);
        }
        return true;
    });
    private static final Statement<?> GET_BLOCK = new InternalStatement((context, arguments) -> {
        if (arguments.isEmpty()) throw new ScriptError("No block position provided.");
        final Location position = getLocation(context, arguments);
        final Block block = (Block) context.manager().executeOnPrimary(context, position::getBlock);
        return new Wrapper<>(new BlockHandle(block, readOnly(context, position)), new BlockHandleKind());
    });
    private static final Statement<?> GET_INVENTORY = new InternalStatement((context, arguments) -> {
        if (arguments.isEmpty()) {
            final InventoryHolder holder = ((InventoryHolder) context.source());
            return new InventoryHandle(holder.getInventory(), false);
        }
        final Object object = arguments.get(0);
        final InventoryHandle handle;
        if (object instanceof Inventory inventory) handle = new InventoryHandle(inventory, true);
        else handle = (InventoryHandle) context.manager().executeOnPrimary(context, () -> {
            if (object instanceof InventoryHolder holder)
                return new InventoryHandle(holder.getInventory(), !holder.equals(context.source()));
            else if (object instanceof Block block && block.getState() instanceof InventoryHolder holder)
                return new InventoryHandle(holder.getInventory(), readOnly(context, block.getLocation()));
            else return null;
        });
        if (handle == null) return null;
        return new Wrapper<>(handle, new InventoryHandleKind());
    });
    private static final Statement<?> GET_PLAYER = new InternalStatement(
        arguments -> Bukkit.getPlayer(arguments.<String>get(0)));

    public MinecraftLibrary() {
        super("time", "wait", "get_player", "get_inventory", "get_block");
    }

    private static int time() {
        final World world = Bukkit.getWorlds().get(0);
        return (int) world.getTime();
    }

    private static boolean readOnly(Context context, Location location) {
        return false;
    }

    public static Location getLocation(Context context, Arguments arguments) {
        if (arguments.isEmpty() && context.source() instanceof Entity entity) return entity.getLocation();
        if (arguments.isEmpty()) throw new ScriptError("No location provided.");
        final Object object = arguments.get(0);
        if (object instanceof Location location) return location;
        else if (object instanceof Vector vector && context.source() instanceof Entity entity)
            return vector.toLocation(entity.getWorld());
        else if (object instanceof Entity entity) return entity.getLocation();
        else if (arguments.size() == 3 && context.source() instanceof Entity entity
            && object instanceof Number x && arguments.get(1) instanceof Number y
            && arguments.get(2) instanceof Number z) {
            return new Location(entity.getWorld(), x.doubleValue(), y.doubleValue(), z.doubleValue());
        } else throw new ScriptError("Value '" + object + "' cannot be converted to a location.");
    }

    @Override
    public Object get(String property) {
        return switch (property) {
            case "time" -> TIME;
            case "wait" -> WAIT;
            case "get_player" -> GET_PLAYER;
            case "get_inventory" -> GET_INVENTORY;
            case "get_block" -> GET_BLOCK;
            default -> null;
        };
    }

    @FunctionalInterface
    interface NumberFunction extends Function<Arguments, Object> {

        Object run(Number number);

        @Override
        default Object apply(Arguments arguments) {
            return this.run(arguments.get(0));
        }

    }

}
