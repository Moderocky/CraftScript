package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.CompareStatement;
import mx.kenzie.craftscript.utility.Comparator;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.Input;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class CompareCompiler implements ElementCompiler<CompareStatement> {

    @Override
    public Instruction.Input<?> compile(CompareStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Input<?> antecedent, consequent, comparator;
        antecedent = compiler.compileStatement(statement.antecedent(), method, builder);
        consequent = compiler.compileStatement(statement.consequent(), method, builder);
        comparator = compiler.compileConstant(statement.comparator(), method, builder);
        return METHOD.of(Comparator.class, Object.class, "compareWrapped", Object.class, Object.class)
            .get(comparator, antecedent, consequent);
    }

}

