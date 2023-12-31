package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record SetterStatement(Statement<?> source, String property, Statement<?> value) implements Statement<Object> {

    @SuppressWarnings("unchecked")
    public static Object execute(Object value, String property, Object result) throws ScriptError {
        final Kind<Object> kind = (Kind<Object>) Kind.of(value);
        return kind.setProperty(Wrapper.unwrap(value), property, Wrapper.unwrap(result));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(Context context) throws ScriptError {
        final Object object = source.execute(context);
        final Object result = value.execute(context);
        final Kind<Object> kind = (Kind<Object>) Kind.of(object);
        return kind.setProperty(Wrapper.unwrap(object), property, Wrapper.unwrap(result));
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        GetterStatement.debug(stream, source, property);
        stream.print(", ");
        stream.print("value=");
        this.value.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        this.source.stringify(stream);
        stream.print('[');
        stream.print(property);
        stream.print('=');
        this.value.stringify(stream);
        stream.print(']');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            this.source.prettyPrint(profile),
            Component.text('[', profile.pop()),
            Component.text(property, profile.highlight()),
            Component.text('=', profile.pop()),
            this.value.prettyPrint(profile),
            Component.text(']', profile.pop())
        ).hoverEvent(Component.textOfChildren(
            Component.text("Sets the '" + property + "' property of an object.", profile.light()),
            Component.newline(),
            this.printReturnType(profile)));
    }

    @Override
    public Class<?> returnType() {
        return Object.class;
    }

}
