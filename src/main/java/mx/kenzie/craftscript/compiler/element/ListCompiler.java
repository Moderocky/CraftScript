package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.ListStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.CompilerBootstrap;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import java.util.List;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class ListCompiler implements ElementCompiler<ListStatement> {

    @Override
    @SuppressWarnings("unchecked")
    public Instruction.Input<?> compile(ListStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Input<Object>[] inputs = new Input[statement.statements().length];
        for (int i = 0; i < inputs.length; i++) {
            final Statement<?> thing = statement.statements()[i];
            inputs[i] = (Input<Object>) compiler.compileStatement(thing, method, builder);
        }
        return METHOD.of(ListStatement.class, List.class, "execute", Object[].class)
            .getStatic(ARRAY.of(Object.class, inputs));
    }

}

