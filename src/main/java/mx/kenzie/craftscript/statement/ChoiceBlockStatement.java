package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;

public record ChoiceBlockStatement(Statement<?>[] either, Statement<?>[] or) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return this.either(context);
    }

    public Object either(Context context) throws ScriptError {
        Object result = null;
        for (final Statement<?> statement : either) result = statement.execute(context);
        return result;
    }

    public Object or(Context context) throws ScriptError {
        Object result = null;
        for (final Statement<?> statement : or) result = statement.execute(context);
        return result;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print("[either=");
        stream.println('[');
        for (final Statement<?> statement : either) {
            stream.print('\t');
            statement.debug(stream);
            stream.println();
        }
        stream.print(']');
        stream.print("[or=");
        stream.println('[');
        for (final Statement<?> statement : or) {
            stream.print('\t');
            statement.debug(stream);
            stream.println();
        }
        stream.print(']');
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.println('{');
        for (final Statement<?> statement : either) {
            stream.print('\t');
            statement.stringify(stream);
            stream.println();
        }
        stream.print("} else {");
        for (final Statement<?> statement : or) {
            stream.print('\t');
            statement.stringify(stream);
            stream.println();
        }
        stream.print('}');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final TextComponent.Builder text = Component.text();
        BlockStatement.prettyPrint(profile, text, either);
        text.append(Component.text(" else ", profile.dark())
            .hoverEvent(Component.text("Otherwise do...", profile.light())));
        BlockStatement.prettyPrint(profile, text, or);
        return text.build();
    }

}
