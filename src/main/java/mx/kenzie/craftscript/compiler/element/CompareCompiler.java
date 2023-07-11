package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.CompareStatement;
import mx.kenzie.craftscript.utility.Comparator;
import mx.kenzie.craftscript.utility.CompilerBootstrap;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class CompareCompiler implements ElementCompiler<CompareStatement> {

    @Override
    public Instruction.Input<?> compile(CompareStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Input<?> antecedent, consequent, comparator;
        antecedent = compiler.compileStatement(statement.antecedent(), method, builder);
        consequent = compiler.compileStatement(statement.consequent(), method, builder);
        comparator = compiler.compileConstant(statement.comparator(), method, builder);
        return METHOD.of(Comparator.class, Object.class, "compare", Object.class, Object.class)
            .get(comparator, antecedent, consequent);
    }

}

