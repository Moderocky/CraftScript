package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.ForStatement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class ForCompiler implements ElementCompiler<ForStatement> {

    @Override
    public Instruction.Input<?> compile(ForStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final PreMethod block = compiler.compile(statement.then() instanceof BlockStatement thing
            ? thing
            : new BlockStatement(statement.then()), builder);
        final String var = statement.assignment().name();
        final Instruction.Input<?> data = compiler.compileStatement(statement.assignment().statement(), method,
            builder);
        return METHOD.of(ForStatement.class, Boolean.class,
                "execute", Context.class, String.class, Object.class, Executable.class)
            .getStatic(
                LOAD_VAR.object(1),
                CONSTANT.of(var),
                data,
                METHOD.metafactory(block, Executable.class, "execute", builder).invoke(LOAD_VAR.object(0))
            );
    }

}

