package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.FunctionStatement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.LOAD_VAR;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class FunctionCompiler implements ElementCompiler<FunctionStatement> {

    @Override
    public Instruction.Input<?> compile(FunctionStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD.metafactory(compiler.compileFunction(statement.executable(), builder),
                Executable.class, "execute", builder)
            .invoke(LOAD_VAR.object(0));
    }

}

