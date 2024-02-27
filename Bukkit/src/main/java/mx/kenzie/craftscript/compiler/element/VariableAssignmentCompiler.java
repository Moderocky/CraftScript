package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.VariableAssignmentStatement;
import mx.kenzie.craftscript.utility.CompilerBootstrap;
import mx.kenzie.craftscript.variable.MagicVariableContainer;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class VariableAssignmentCompiler implements ElementCompiler<VariableAssignmentStatement> {

    @Override
    public Instruction.Input<?> compile(VariableAssignmentStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Input<?> input = compiler.compileStatement(statement.statement(), method, builder);
        if (compiler.isFastVariables()) {
            compiler.notifyVariable(statement.name());
            return MagicVariableContainer.setVariable(statement.name())
                .get(LOAD_VAR.object(3), input);
        } else return METHOD
            .of(CompilerBootstrap.class, Object.class, "setVariable", Context.class, String.class, Object.class)
            .getStatic(LOAD_VAR.object(1), CONSTANT.of(statement.name()), input);
    }

}
