package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;
import net.kyori.adventure.text.Component;

public record AnonymousScript(
    Statement<?>... statements) implements AbstractScript, Statement<Object>, Executable<Object> {

    @Override
    public String toString() {
        return this.name();
    }

    @Override
    public String name() {
        return "???.script";
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return AbstractScript.super.prettyPrint(profile)
            .hoverEvent(Component.textOfChildren(
                Component.text("An unnamed runnable script object.", profile.light()),
                this.printReturnType(profile)
            ));
    }

}
