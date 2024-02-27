package mx.kenzie.craftscript.environment.kind;

import mx.kenzie.craftscript.environment.MinecraftLibrary;
import mx.kenzie.craftscript.environment.world.ItemHandle;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.core.MainThreadStatement;

public class ItemHandleKind extends Kind<ItemHandle> {

    public ItemHandleKind() {
        super(ItemHandle.class);
    }

    @Override
    public Object getProperty(ItemHandle handle, String s) {
        if (handle == null) return null;
        return switch (s) {
            case "read_only" -> handle.readOnly();
            case "type" -> handle.item() != null ? handle.item().getType().getKey() : null;
            case "amount" -> handle.item() != null ? handle.item().getAmount() : 0;
            case "drop" -> new MainThreadStatement(arguments -> handle.drop(MinecraftLibrary
                .getLocation(Context.requireLocalContext(), arguments)));
            default -> null;
        };

    }

    @Override
    public String toString() {
        return "#item";
    }

}
