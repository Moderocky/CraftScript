package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;

public record SetterStatement(Statement<?> source, String property, Statement<?> value) implements Statement<Object> {

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
        stream.print("property=");
        stream.print(property);
        stream.print(", ");
        stream.print("source=");
        this.source.debug(stream);
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

}
