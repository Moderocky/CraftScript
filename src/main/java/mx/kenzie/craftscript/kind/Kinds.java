package mx.kenzie.craftscript.kind;

import java.util.ArrayList;
import java.util.List;

public interface Kinds {

    List<Kind<?>> kinds = new ArrayList<>(List.of(
        StringKind.STRING,
        PlayerKind.PLAYER,
        CommandSenderKind.COMMAND_SENDER,
        BlockKind.BLOCK,
        BlockDataKind.BLOCK_DATA,
        MaterialKind.MATERIAL,
        LocationKind.LOCATION,
        VectorKind.VECTOR,
        StatementKind.STATEMENT,
        ExecutableKind.EXECUTABLE,
        EventKind.EVENT,
        CustomKind.STRUCTURE,
        MapKind.MAP,
        ListKind.LIST,
        CollectionKind.COLLECTION,
        NullKind.NULL,
        LibraryKind.LIBRARY,
        IntegerKind.INTEGER,
        NumberKind.NUMBER,
        EnumKind.ENUM,
        KindKind.KIND
    ));

    static Kind<?>[] values() {
        return kinds.toArray(new Kind[0]);
    }

}
