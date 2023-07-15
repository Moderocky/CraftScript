package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.LocalFunctionStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

public class LocalFunctionCompiler implements ElementCompiler<LocalFunctionStatement> {

    @Override
    public Instruction.Input<?> compile(LocalFunctionStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Instruction.Input<?> function = compiler.compileStatement(statement.statement(), method, builder), data;
        if (statement.data() == null) data = Instruction.NULL;
        else data = compiler.compileStatement(statement.data(), method, builder);
        return RunCompiler.compile(function, data, builder, compiler);
    }

}

