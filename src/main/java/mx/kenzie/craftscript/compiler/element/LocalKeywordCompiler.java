package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.LocalKeywordStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class LocalKeywordCompiler implements ElementCompiler<LocalKeywordStatement> {

    @Override
    public Instruction.Input<?> compile(LocalKeywordStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD
            .of(LocalKeywordStatement.class, Object.class, "execute", Context.class, String.class)
            .getStatic(LOAD_VAR.object(1), CONSTANT.of(statement.name()));
    }

}

