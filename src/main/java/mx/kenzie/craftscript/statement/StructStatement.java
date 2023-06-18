package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.StructObject;

import java.io.PrintStream;

public record StructStatement(BlockStatement block) implements Statement<StructObject> {

    @Override
    public StructObject execute(Context context) throws ScriptError {
        final StructObject object = new StructObject();
        final Context sub = new Context(context.source(), context.manager(), object, context.data());
        this.block.execute(sub);
        return object;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("block=");
        this.block.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("struct ");
        this.block.stringify(stream);
    }

}
