package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record SyntaxStatement(TextStatement pattern, Statement<?> function) implements Statement<Void> {

    @Override
    public Class<Void> returnType() {
        return Void.class;
    }

    @Override
    public Void execute(Context context) throws ScriptError {
        return null;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("pattern=");
        this.pattern.debug(stream);
        stream.print(", function=");
        this.function.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("syntax ");
        this.pattern.stringify(stream);
        stream.print(" = ");
        this.function.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("syntax ", profile.dark()),
            this.pattern.prettyPrint(profile),
            Component.text(" = ", profile.pop()),
            this.function.prettyPrint(profile)
        ).hoverEvent(Component.textOfChildren(
            Component.text("Define custom statement syntax available in this file.", profile.light()),
            Component.newline(), this.printReturnType(profile)));
    }

}
