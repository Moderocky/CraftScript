package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.VariableStatement;
import mx.kenzie.craftscript.utility.CompilerBootstrap;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class VariableCompiler implements ElementCompiler<VariableStatement> {

    @Override
    public Input<?> compile(VariableStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        return METHOD
            .of(CompilerBootstrap.class, Object.class, "getVariable", Context.class, String.class)
            .getStatic(LOAD_VAR.object(1), CONSTANT.of(statement.name()));
    }

}
