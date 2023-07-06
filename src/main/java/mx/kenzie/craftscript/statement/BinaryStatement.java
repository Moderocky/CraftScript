package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public interface BinaryStatement<Result> extends Statement<Result> {

    Statement<?> antecedent();

    Statement<?> consequent();

    String symbol();

    @Override
    default void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("antecedent=");
        this.antecedent().debug(stream);
        stream.print(", ");
        stream.print("consequent=");
        this.consequent().debug(stream);
        stream.print(']');
    }

    @Override
    default void stringify(PrintStream stream) {
        this.antecedent().stringify(stream);
        stream.print(this.symbol());
        this.consequent().stringify(stream);
    }

    @Override
    default Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            this.antecedent().prettyPrint(profile),
            Component.space(),
            Component.text(this.symbol(), profile.pop()),
            Component.space(),
            this.consequent().prettyPrint(profile)
        );
    }

}
