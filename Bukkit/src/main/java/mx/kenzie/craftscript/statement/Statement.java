package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.PrettyPrinter;
import net.kyori.adventure.text.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public interface Statement<Result> extends Executable<Result> {

    default void debug(PrintStream stream) {
        stream.print(this);
    }

    void stringify(PrintStream stream);

    default String stringify() {
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (PrintStream stream = new PrintStream(output)) {
                this.stringify(stream);
                stream.flush();
            }
            return output.toString();
        } finally {
            PrettyPrinter.reset();
        }
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
        final Class<?> result = this.returnType();
        final String name;
        if (result == Object.class) name = "Any";
        else name = result.getSimpleName()
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
            .replaceAll("([a-z])([A-Z])", "$1 $2");
        return Component.translatable("script.statement.outcome", "Outcome: %s",
            Component.text(name, profile.highlight())).color(profile.light());
    }

}
