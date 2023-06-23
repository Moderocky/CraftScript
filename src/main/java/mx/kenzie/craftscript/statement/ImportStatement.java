package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptManager;
import mx.kenzie.craftscript.variable.VariableContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;

public record ImportStatement(String... names) implements Statement<Boolean> {

    @Override
    public Boolean execute(Context context) throws ScriptError {
        final VariableContainer container = context.variables();
        final ScriptManager manager = context.manager();
        for (final String name : names) {
            final AbstractScript script = manager.getScript(name + ".script");
            if (script == null) throw new ScriptError("The script '" + name + ".script' is not loaded.");
            final Object result = RunStatement.run(script, context, new VariableContainer());
            container.put(name, result);
        }
        return true;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print(String.join(", ", names));
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("import [");
        stream.print(String.join(", ", names));
        stream.print(']');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text("import ", profile.dark()));
        builder.append(Component.text('[', profile.pop()));
        for (int i = 0; i < names.length; i++) {
            if (i > 0) builder.append(Component.text(", ", profile.pop()));
            builder.append(Component.text(names[i], profile.highlight()));
        }
        builder.append(Component.text(']', profile.pop()));
        return builder.build().hoverEvent(Component.text("Imports resources from an external program."));
    }

}
