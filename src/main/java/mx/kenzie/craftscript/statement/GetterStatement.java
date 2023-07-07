package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record GetterStatement(Statement<?> source, String property) implements Statement<Object> {

    static void debug(PrintStream stream, Statement<?> source, String property) {
        stream.print("property=");
        stream.print(property);
        stream.print(", ");
        stream.print("source=");
        source.debug(stream);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(Context context) throws ScriptError {
        final Object value = source.execute(context);
        final Kind<Object> kind = (Kind<Object>) Kind.of(value);
        return Wrapper.of(kind.getProperty(Wrapper.unwrap(value), property));
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        GetterStatement.debug(stream, source, property);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        this.source.stringify(stream);
        stream.print('[');
        stream.print(property);
        stream.print(']');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            this.source.prettyPrint(profile),
            Component.text('[', profile.pop()),
            Component.text(property, profile.highlight()),
            Component.text(']', profile.pop())
        ).hoverEvent(Component.text("Gets the '" + property + "' property of an object.", profile.light()));
    }

    @Override
    public Class<?> returnType() {
        final Kind<?> kind = KindStatement.lookForKind(source);
        return kind.getTypeHint(property); // can we find some way to infer this ?
    }

}
