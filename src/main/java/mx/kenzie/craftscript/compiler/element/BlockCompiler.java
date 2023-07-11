package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.utility.CompilerBootstrap;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class BlockCompiler implements ElementCompiler<BlockStatement> {

    @Override
    public Instruction.Input<?> compile(BlockStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final PreMethod child = compiler.compile(statement, builder);
        return METHOD.of(builder, child)
            .get(LOAD_VAR.self(), LOAD_VAR.object(1));
    }

}

