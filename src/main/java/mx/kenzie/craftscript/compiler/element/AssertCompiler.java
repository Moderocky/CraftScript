package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.AssertStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.CONSTANT;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class AssertCompiler implements ElementCompiler<AssertStatement> {

    @Override
    public Instruction.Input<?> compile(AssertStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD
            .of(AssertStatement.class, Object.class, "check", Object.class, String.class)
            .getStatic(compiler.compileStatement(statement.check(), method, builder), CONSTANT.of(statement.check().stringify()));
    }

}

