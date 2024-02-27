package mx.kenzie.craftscript.environment.kind;

import mx.kenzie.craftscript.environment.world.BlockHandle;
import mx.kenzie.craftscript.kind.BlockKind;
import mx.kenzie.craftscript.kind.Kind;

public class BlockHandleKind extends Kind<BlockHandle> {

    public BlockHandleKind() {
        super(BlockHandle.class);
    }

    @Override
    public Object getProperty(BlockHandle handle, String s) {
        if (handle == null) return null;
        return switch (s) {
            case "read_only" -> handle.readOnly();
            case "is_empty" -> handle.block().isEmpty();
            case "inventory" -> handle.getInventory();
            case "type" -> handle.getType().getKey();
            default -> new BlockKind().getProperty(handle.block(), s);
        };
    }

    @Override
    public String toString() {
        return "#block";
    }

}
