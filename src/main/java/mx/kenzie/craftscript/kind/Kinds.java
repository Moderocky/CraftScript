package mx.kenzie.craftscript.kind;

import org.bukkit.block.TileState;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Kinds {

    StringKind STRING = StringKind.STRING;
    PlayerKind PLAYER = PlayerKind.PLAYER;
    CommandSenderKind<CommandSender> COMMAND_SENDER = CommandSenderKind.COMMAND_SENDER;
    BlockEntityKind<TileState> BLOCK_ENTITY = BlockEntityKind.BLOCK_ENTITY;
    BlockKind BLOCK = BlockKind.BLOCK;
    BlockDataKind BLOCK_DATA = BlockDataKind.BLOCK_DATA;
    MaterialKind MATERIAL = MaterialKind.MATERIAL;
    LocationKind LOCATION = LocationKind.LOCATION;
    VectorKind VECTOR = VectorKind.VECTOR;
    StatementKind STATEMENT = StatementKind.STATEMENT;
    ExecutableKind EXECUTABLE = ExecutableKind.EXECUTABLE;
    EventKind EVENT = EventKind.EVENT;
    CustomKind STRUCTURE = CustomKind.STRUCTURE;
    MapKind MAP = MapKind.MAP;
    ListKind LIST = ListKind.LIST;
    CollectionKind<Collection<?>> COLLECTION = CollectionKind.COLLECTION;
    NullKind NULL = NullKind.NULL;
    LibraryKind LIBRARY = LibraryKind.LIBRARY;
    IntegerKind INTEGER = IntegerKind.INTEGER;
    NumberKind NUMBER = NumberKind.NUMBER;
    EnumKind<?> ENUM = EnumKind.ENUM;
    KindKind KIND = KindKind.KIND;

    /**
     * Represents an unknown kind of variable.
     * <p>
     * The ANY variable is of type UnknownKind and is used to indicate an unknown or unspecified kind of variable.
     * It can be utilized in cases where the specific kind of variable is not yet or cannot be determined.
     */
    UnknownKind ANY = UnknownKind.ANY;


    List<Kind<?>> kinds = new ArrayList<>(List.<Kind<?>>of(
        STRING,
        PLAYER,
        COMMAND_SENDER,
        BLOCK_ENTITY,
        BLOCK,
        BLOCK_DATA,
        MATERIAL,
        LOCATION,
        VECTOR,
        STATEMENT,
        EXECUTABLE,
        EVENT,
        STRUCTURE,
        MAP,
        LIST,
        COLLECTION,
        NULL,
        LIBRARY,
        INTEGER,
        NUMBER,
        ENUM,
        KIND
    ));

    static Kind<?>[] values() {
        return kinds.toArray(new Kind[0]);
    }

}
