package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.emitter.EventListener;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.ListenerStatement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class ListenerCompiler implements ElementCompiler<ListenerStatement> {

    @Override
    public Instruction.Input<?> compile(ListenerStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Input<?> key = compiler.compileStatement(statement.key(), method, builder);
        final Input<?> data = statement.details() != null
            ? compiler.compileStatement(statement.details(), method, builder)
            : Input.NULL;
        final Input<?> function = METHOD
            .metafactory(compiler.compileFunction(statement.task(), builder), Executable.class, "execute", builder)
            .invoke(LOAD_VAR.object(0));
        return METHOD.of(ListenerStatement.class, EventListener.class,
                "execute", Context.class, Object.class, Object.class, Executable.class)
            .getStatic(LOAD_VAR.object(1), key, data, function);
    }

}

