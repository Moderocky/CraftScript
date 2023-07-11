package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.ScriptCompileError;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.LocalSyntaxStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

public class LocalSyntaxCompiler implements ElementCompiler<LocalSyntaxStatement> {

    @Override
    public Instruction.Input<?> compile(LocalSyntaxStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        throw new ScriptCompileError("Local syntax is not currently supported."); // todo
    }

}

