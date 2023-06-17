package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;

import java.io.PrintStream;

public record KindStatement(String name) implements Statement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        for (final Kind<?> kind : context.getKinds()) {
            if (kind.toString().equals('#' + name)) return kind;
        }
        throw new ScriptError("Unknown type #" + name + ".");
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
        stream.print('#');
        stream.print(name);
    }

}
