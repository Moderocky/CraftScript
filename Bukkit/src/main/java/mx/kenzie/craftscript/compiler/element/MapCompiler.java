package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.MapStatement;
import mx.kenzie.craftscript.utility.Container;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.LOAD_VAR;
import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class MapCompiler implements ElementCompiler<MapStatement> {

    @Override
    public Instruction.Input<?> compile(MapStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final PreMethod block = compiler.compile(new BlockStatement(statement.statements()), builder);
        return METHOD.of(MapStatement.class, Container.class, "execute", Context.class, Executable.class)
            .getStatic(
                LOAD_VAR.object(1),
                METHOD.metafactory(block, Executable.class, "execute", builder).invoke(LOAD_VAR.object(0))
            );
    }

}

