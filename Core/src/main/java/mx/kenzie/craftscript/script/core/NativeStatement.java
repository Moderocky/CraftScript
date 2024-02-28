package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Bridge;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public interface NativeStatement<Type> extends Statement<Type> {

    @Override
    default void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(']');
    }

    @Override
    default void stringify(PrintStream stream) {
        stream.print("<native code>");
    }

    @Override
    default Component prettyPrint(ColorProfile profile) {
        return Statement.super.prettyPrint(profile)
            .hoverEvent(Component.textOfChildren(Component.text("This comes from a system library.", profile.light()),
                this.printReturnType(profile)));
    }

    default Class<? extends Type> returnType() {
        return Bridge.cast(Object.class);
    }

}
