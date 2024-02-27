package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.InvertStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.LOAD_VAR;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class InvertCompiler implements ElementCompiler<InvertStatement> {

    @Override
    public Instruction.Input<?> compile(InvertStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD.of(InvertStatement.class, Boolean.class, "execute", Context.class, Object.class)
            .getStatic(LOAD_VAR.object(1), compiler.compileStatement(statement.check(), method, builder));
    }

}

