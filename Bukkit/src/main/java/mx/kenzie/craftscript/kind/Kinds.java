package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.utility.Container;
import org.bukkit.block.TileState;
import org.bukkit.command.CommandSender;

import java.util.*;

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
    ContainerKind<Container> CONTAINER = ContainerKind.CONTAINER;
    MapKind<Object, Object, Map<Object, Object>> MAP = MapKind.MAP;
    ListKind LIST = ListKind.LIST;
    SetKind SET = SetKind.SET;
    CollectionKind<Collection<?>> COLLECTION = CollectionKind.COLLECTION;
    NullKind NULL = NullKind.NULL;
    LibraryKind LIBRARY = LibraryKind.LIBRARY;
    IntegerKind INTEGER = IntegerKind.INTEGER;
    NumberKind<Number> NUMBER = NumberKind.NUMBER;
    EnumKind<?> ENUM = EnumKind.ENUM;
    KindKind KIND = KindKind.KIND;

    /**
     * Represents an unknown kind of variable.
     * <p>
     * The ANY variable is of type UnknownKind and is used to indicate an unknown or unspecified kind of variable.
     * It can be utilized in cases where the specific kind of variable is not yet or cannot be determined.
     */
    UnknownKind ANY = UnknownKind.ANY;


    /**
     * An immutable list of the default kinds of object.
     *
     * <p>This list contains various {@link Kind} objects representing different types of variables.
     * It can be used to store and manipulate variables of different types.</p>
     *
     * <p>The list includes the following kinds:</p>
     * <ul>
     *   <li>{@link Kinds#STRING}: Represents a variable of type String.</li>
     *   <li>{@link Kinds#PLAYER}: Represents a variable of type Player.</li>
     *   <li>{@link Kinds#COMMAND_SENDER}: Represents a variable of type CommandSender.</li>
     *   <li>{@link Kinds#BLOCK_ENTITY}: Represents a variable of type BlockEntity.</li>
     *   <li>{@link Kinds#BLOCK}: Represents a variable of type Block.</li>
     *   <li>{@link Kinds#BLOCK_DATA}: Represents a variable of type BlockData.</li>
     *   <li>{@link Kinds#MATERIAL}: Represents a variable of type Material.</li>
     *   <li>{@link Kinds#LOCATION}: Represents a variable of type Location.</li>
     *   <li>{@link Kinds#VECTOR}: Represents a variable of type Vector.</li>
     *   <li>{@link Kinds#STATEMENT}: Represents a variable of type Statement.</li>
     *   <li>{@link Kinds#EXECUTABLE}: Represents a variable of type Executable.</li>
     *   <li>{@link Kinds#EVENT}: Represents a variable of type Event.</li>
     *   <li>{@link Kinds#LIBRARY}: Represents a variable of type Library.</li>
     *   <li>{@link Kinds#STRUCTURE}: Represents a variable of type Structure.</li>
     *   <li>{@link Kinds#CONTAINER}: Represents a variable of type Container.</li>
     *   <li>{@link Kinds#MAP}: Represents a variable of type Map.</li>
     *   <li>{@link Kinds#LIST}: Represents a variable of type List.</li>
     *   <li>{@link Kinds#SET}: Represents a variable of type Set.</li>
     *   <li>{@link Kinds#COLLECTION}: Represents a variable of type Collection.</li>
     *   <li>{@link Kinds#NULL}: Represents a variable with null value.</li>
     *   <li>{@link Kinds#INTEGER}: Represents a variable of type Integer.</li>
     *   <li>{@link Kinds#NUMBER}: Represents a variable of type Number.</li>
     *   <li>{@link Kinds#ENUM}: Represents a variable of type Enum.</li>
     *   <li>{@link Kinds#KIND}: Represents a variable of type Kind.</li>
     * </ul>
     *
     * @see Kind
     */
    List<Kind<?>> DEFAULT_KINDS = List.of(
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
        LIBRARY,
        STRUCTURE,
        CONTAINER,
        MAP,
        LIST,
        SET,
        COLLECTION,
        NULL,
        INTEGER,
        NUMBER,
        ENUM,
        KIND
    );

    /**
     * A mutable set copy of the default kinds list.
     * This is designed for extensions to add their own kinds.
     * <p>
     * This set is linked to preserve ordering.
     */
    Set<Kind<?>> kinds = new LinkedHashSet<>(DEFAULT_KINDS);

    static Kind<?>[] values() {
        return kinds.toArray(new Kind[0]);
    }

}
