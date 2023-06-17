package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Context;
import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.variable.UnknownObject;

import java.io.PrintStream;

public record TypeStatement(String name, BlockStatement block) implements Statement<UnknownObject> {

    @Override
    public UnknownObject execute(Context context) throws ScriptError {
        final UnknownObject object = new UnknownObject(name);
        final Context sub = new Context(context.source(), context.manager(), object, context.data());
        this.block.execute(sub);
        return object;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("name=");
        stream.print(name);
        stream.print(", ");
        stream.print("init=");
        this.block.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("type ");
        stream.print(name);
        stream.print(' ');
        this.block.stringify(stream);
    }

}
