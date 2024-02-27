package mx.kenzie.craftscript.kind;

import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;

public class BlockEntityKind<Type extends BlockState> extends Kind<Type> {

    public static final BlockEntityKind<TileState> BLOCK_ENTITY = new BlockEntityKind<>();

    @SuppressWarnings("unchecked")
    public BlockEntityKind() {
        super((Class<Type>) TileState.class);
    }

    protected BlockEntityKind(Class<Type> type) {
        super(type);
    }

    @Override
    public Object getProperty(Type thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "block" -> thing.getBlock();
            default -> BlockKind.BLOCK.getProperty(thing.getBlock(), property);
        };
    }

    @Override
    public String[] getProperties() {
        return new String[] {"block"};
    }

    @Override
    public String toString() {
        return "#blockentity";
    }

}
