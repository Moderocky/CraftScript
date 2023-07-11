package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.GetterStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.CONSTANT;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class GetterCompiler implements ElementCompiler<GetterStatement> {

    @Override
    public Instruction.Input<?> compile(GetterStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD.of(GetterStatement.class, Object.class, "execute", Object.class, String.class)
            .getStatic(
                compiler.compileStatement(statement.source(), method, builder),
                CONSTANT.of(statement.property()));
    }

}

