package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.StructObject;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record StructStatement(BlockStatement block) implements Statement<StructObject> {

    public static StructObject execute(Context context, Executable<?> block) throws ScriptError {
        final StructObject object = new StructObject();
        final Context sub = new Context(context.source(), context.manager(), object, context.data());
        block.execute(sub);
        object.freeze();
        return object;
    }

    @Override
    public StructObject execute(Context context) throws ScriptError {
        return execute(context, block);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("block=");
        this.block.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("struct ");
        this.block.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("struct ", profile.dark()),
            this.block.prettyPrint(profile)
        ).hoverEvent(
            Component.textOfChildren(Component.text("A custom object with a set of properties.", profile.light()),
                Component.newline(), this.printReturnType(profile)));
    }

    @Override
    public Class<? extends StructObject> returnType() {
        return StructObject.class;
    }

}
