package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.RequireStatement;
import mx.kenzie.craftscript.utility.CompilerBootstrap;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class RequireCompiler implements ElementCompiler<RequireStatement> {

    @Override
    public Instruction.Input<?> compile(RequireStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD.of(RequireStatement.class, Boolean.class, "execute", Context.class, String[].class)
            .getStatic(LOAD_VAR.object(1), compiler.compileInputArray(statement.names(), builder));
    }

}

