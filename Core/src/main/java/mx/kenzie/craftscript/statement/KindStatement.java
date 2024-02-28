package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.kind.Kinds;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Bridge;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record KindStatement(String name) implements Statement<Kind<?>> {

    public static Kind<?> lookForKind(Statement<?> statement) {
        for (final Kind<?> kind : Kinds.kinds) {
            if (kind.getType().isAssignableFrom(statement.returnType())) return kind;
        }
        return Kinds.ANY;
    }

    public static Kind<?> execute(Context<?> context, String name) {
        for (final Kind<?> kind : context.getKinds()) {
            if (kind.toString().equals('#' + name)) return kind;
        }
        throw new ScriptError("Unknown type #" + name + ".");
    }

    @Override
    public Kind<?> execute(Context<?> context) throws ScriptError {
        for (final Kind<?> kind : context.getKinds()) {
            if (kind.toString().equals('#' + name)) return kind;
        }
        throw new ScriptError("Unknown type #" + name + ".");
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("name=");
        stream.print(name);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print('#');
        stream.print(name);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text('#', profile.pop()),
            Component.text(name, profile.highlight())
        ).hoverEvent(Component.textOfChildren(Component.text("An object type.", profile.light()), Component.newline(),
            this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Kind<?>> returnType() {
        if (!Kinds.kinds.isEmpty()) for (final Kind<?> kind : Kinds.kinds) {
            if (kind.toString().equals('#' + name)) return Bridge.cast(kind.getClass());
        }
        return Bridge.cast(Kind.class);
    }

}
