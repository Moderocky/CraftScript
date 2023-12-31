package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record ScriptStatement(String name) implements Statement<AbstractScript>, EvaluatedStatement<AbstractScript> {

    public static AbstractScript execute(Context context, String name) {
        final AbstractScript script = context.manager().getScript(name);
        if (script == null) throw new ScriptError("Script '" + name + "' is not loaded.");
        return script;
    }

    @Override
    public AbstractScript execute(Context context) throws ScriptError {
        final AbstractScript script = context.manager().getScript(name);
        if (script == null) throw new ScriptError("Script '" + name + "' is not loaded.");
        return script;
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
        stream.print(name);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.text(name, profile.light())
            .hoverEvent(
                Component.textOfChildren(Component.text("An executable script.", profile.light()), Component.newline(),
                    this.printReturnType(profile)));
    }

    @Override
    public Class<? extends AbstractScript> returnType() {
        return AbstractScript.class;
    }

    @Override
    public Class<?> evaluatedReturnType() {
        return Object.class;
    }

}
