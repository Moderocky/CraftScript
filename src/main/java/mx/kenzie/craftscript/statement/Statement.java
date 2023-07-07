package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.utility.Executable;
import net.kyori.adventure.text.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public interface Statement<Result> extends Executable<Result> {

    default void debug(PrintStream stream) {
        stream.print(this);
    }

    void stringify(PrintStream stream);

    default String stringify() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(output)) {
            this.stringify(stream);
        }
        return output.toString();
    }

    default Component prettyPrint(ColorProfile profile) {
        if (this.knowsReturnType()) return Component.text(this.stringify(), profile.light())
            .hoverEvent(this.printReturnType(profile));
        return Component.text(this.stringify(), profile.light());
    }

    default boolean knowsReturnType() {
        return this.returnType() != Object.class;
    }

    Class<? extends Result> returnType();

    default Component printReturnType(ColorProfile profile) {
        final String string = KindStatement.lookForKind(this).toString();
        if (string.startsWith("#")) return Component.textOfChildren(
            Component.text('#', profile.pop()),
            Component.text(string.substring(1), profile.highlight())
        );
        return Component.text(string, profile.highlight());
    }

}
