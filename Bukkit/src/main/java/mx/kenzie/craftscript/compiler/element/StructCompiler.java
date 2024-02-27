package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.StructStatement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.StructObject;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.LOAD_VAR;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class StructCompiler implements ElementCompiler<StructStatement> {

    @Override
    public Instruction.Input<?> compile(StructStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final boolean fast = compiler.isFastVariables();
        compiler.setFastVariables(false);
        final PreMethod block = compiler.compile(statement.block(), builder);
        compiler.setFastVariables(fast);
        return METHOD.of(StructStatement.class, StructObject.class, "execute", Context.class, Executable.class)
            .getStatic(
                LOAD_VAR.object(1),
                METHOD.metafactory(block, Executable.class, "execute", builder).invoke(LOAD_VAR.object(0))
            );
    }

}

