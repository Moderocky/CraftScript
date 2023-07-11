package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

public class InterpolationCompiler implements ElementCompiler<InterpolationStatement> {

    @Override
    public Instruction.Input<?> compile(InterpolationStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return compiler.compileStatement(statement.statement(), method, builder);
    }

}

