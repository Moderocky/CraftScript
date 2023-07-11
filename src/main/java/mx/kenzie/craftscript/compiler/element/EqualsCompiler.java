package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.EqualsStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.LOAD_VAR;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class EqualsCompiler implements ElementCompiler<EqualsStatement> {

    @Override
    public Instruction.Input<?> compile(EqualsStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Instruction.Input<?> antecedent, consequent;
        antecedent = compiler.compileStatement(statement.antecedent(), method, builder);
        consequent = compiler.compileStatement(statement.consequent(), method, builder);
        return METHOD.of(EqualsStatement.class, Boolean.class, "execute", Object.class, Object.class)
            .getStatic(antecedent, consequent);
    }

}

