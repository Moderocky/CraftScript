package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;

import java.io.PrintStream;
import java.util.Map;

public record RunStatement(Statement<?> statement, Statement<?> data) implements Statement<Object> {

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(Context context) throws ScriptError {
        final Object result = Wrapper.unwrap(statement.execute(context));
        final VariableContainer variables;
        if (data != null) {
            final Object found = Wrapper.unwrap(data.execute(context));
            if (found instanceof Map<?, ?> map) variables = new VariableContainer((Map<String, Object>) map);
            else {
                variables = new VariableContainer();
                variables.put("$parameters", found);
            }
        } else variables = new VariableContainer();
        final Context sub = new Context(context.source(), context.manager(), variables, context.data().clone());
        Context.setLocalContext(sub);
        try {
            if (result instanceof Executable<?> executable) return executable.execute(sub);
            else if (result instanceof Runnable runnable) {
                runnable.run();
                return null;
            } else throw new ScriptError("Unable to run object '" + result + "'.");
        } finally {
            Context.setLocalContext(context);
        }
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        stream.print("statement=");
        this.statement.debug(stream);
        if (data != null) {
            stream.print(", ");
            stream.print("data=");
            this.data.debug(stream);
        }
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("run ");
        this.statement.stringify(stream);
        stream.print(' ');
        if (data != null) this.data.stringify(stream);
    }

}
