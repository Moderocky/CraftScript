package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptRuntimeError;
import mx.kenzie.craftscript.statement.*;
import mx.kenzie.craftscript.utility.Entries;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.VariableHelper;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record SuperFunction<Result>(Executable<Result> executable,
                                    Statement<?>... parameters) implements Executable<Result> {

    public SuperFunction {
        for (Statement<?> parameter : parameters) {
            if (parameter instanceof VariableAssignmentStatement assign) {
                VariableHelper.instance().assign(assign.name(), assign.statement());
            } else if (parameter instanceof VariableReferenceStatement reference) {
                VariableHelper.instance().assignIfAbsent(reference.name(), Object.class);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <Result> SuperFunction<Result> of(Object object) {
        object = Wrapper.unwrap(object);
        if (object instanceof SuperFunction<?> function) return (SuperFunction<Result>) function;
        else if (object instanceof Executable<?> executable)
            return (SuperFunction<Result>) new SuperFunction<>(executable);
        else throw new ScriptRuntimeError("Can't turn '" + object + "' into a super-function");
    }

    public static Builder of() {
        return new Builder();
    }

    @Override
    public Result execute(Context<?> context) throws ScriptError {
        return executable.execute(context);
    }

    public void prepare(Context<?> context, Arguments arguments) {
        for (Statement<?> parameter : parameters) parameter.execute(context);
        final VariableContainer variables = context.variables();
        for (int i = 0; i < parameters.length; i++) {
            final Statement<?> parameter = parameters[i];
            if (parameter instanceof VariableReferenceStatement found) {
                final Object result = arguments.get(found.name(), i);
                if (result != null) variables.put(found.name(), result);
            }
        }
    }

    public record Arguments(Argument... arguments) {

        public Object get(int index) {
            if (index >= arguments.length) return null;
            return arguments[index].value;
        }

        public Object get(String name) {
            if (name != null) for (Argument argument : arguments) {
                if (argument.name.equals(name)) return argument.value;
            }
            return null;
        }

        public Object get(String name, int index) {
            if (name != null) for (Argument argument : arguments) {
                if (name.equals(argument.name)) return argument.value;
            }
            return this.get(index);
        }

    }

    public record Argument(String name, Object value) {

    }

    public static class Builder {

        final List<Statement<?>> list = new ArrayList<>();

        public Builder arg(String name) {
            this.list.add(new VariableStatement(name));
            return this;
        }

        public Builder arg(String name, Object value) {
            this.list.add(new VariableAssignmentStatement(name, new LiteralStatement(value)));
            return this;
        }

        public <Result> SuperFunction<Result> runs(Function<Entries, Result> function) {
            return new SuperFunction<>(context -> function.apply(context.variables()), list.toArray(new Statement[0]));
        }

    }

}
