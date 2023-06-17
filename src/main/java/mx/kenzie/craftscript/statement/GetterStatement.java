package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;

public record GetterStatement(Statement<?> source, String property) implements Statement<Object> {

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
        stream.print("property=");
        stream.print(property);
        stream.print(", ");
        stream.print("source=");
        this.source.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        this.source.stringify(stream);
        stream.print('[');
        stream.print(property);
        stream.print(']');
    }

}
