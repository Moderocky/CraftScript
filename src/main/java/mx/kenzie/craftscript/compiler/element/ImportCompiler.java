package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.ImportStatement;
import mx.kenzie.craftscript.utility.CompilerBootstrap;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class ImportCompiler implements ElementCompiler<ImportStatement> {

    @Override
    public Instruction.Input<?> compile(ImportStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD.of(ImportStatement.class, Boolean.class, "execute", Context.class, String[].class)
            .getStatic(LOAD_VAR.object(1), compiler.compileInputArray(statement.names(), builder));
    }

}

