package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.DoStatement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.LOAD_VAR;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class DoCompiler implements ElementCompiler<DoStatement> {

    @Override
    public Instruction.Input<?> compile(DoStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final PreMethod block = compiler.compile(statement.then() instanceof BlockStatement thing
            ? thing
            : new BlockStatement(statement.then()), builder);
        final Instruction.Input<?> source = compiler.compileStatement(statement.source(), method,
            builder);
        return METHOD.of(DoStatement.class, Object.class,
                "execute", Context.class, Object.class, Executable.class)
            .getStatic(
                LOAD_VAR.object(1),
                source,
                METHOD.metafactory(block, Executable.class, "execute", builder).invoke(LOAD_VAR.object(0))
            );
    }

}

