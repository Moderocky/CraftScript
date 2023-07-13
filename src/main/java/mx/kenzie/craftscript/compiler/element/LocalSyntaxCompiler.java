package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.LocalSyntaxStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class LocalSyntaxCompiler implements ElementCompiler<LocalSyntaxStatement> {

    @Override
    @SuppressWarnings("unchecked")
    public Instruction.Input<?> compile(LocalSyntaxStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final BlockStatement child;
        if (statement.function() instanceof BlockStatement block) child = block;
        else child = new BlockStatement(statement.function());
        final PreMethod block = compiler.compile(child, builder);
        final String[] keys = statement.data().keySet().toArray(new String[0]);
        final Statement<?>[] values = statement.data().values().toArray(new Statement[0]);
        final Input<Object>[] inputs = new Input[values.length];
        for (int i = 0; i < values.length; i++) {
            inputs[i] = (Input<Object>) compiler.compileStatement(values[i], method, builder);
        }
        return METHOD.of(LocalSyntaxStatement.class, Object.class,
                "execute", Context.class, Executable.class, String[].class, Object[].class)
            .getStatic(
                LOAD_VAR.object(1),
                METHOD.metafactory(block, Executable.class, "execute", builder).invoke(LOAD_VAR.object(0)),
                compiler.compileInputArray(keys, builder),
                ARRAY.of(Object.class, inputs)
            );
    }

}

