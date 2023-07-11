package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.CommandStatement;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class CommandCompiler implements ElementCompiler<CommandStatement> {

    @Override
    public Instruction.Input<?> compile(CommandStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final InterpolationStatement[] statements = statement.interpolations();
        final String[] keys = new String[statements.length];
        final Instruction.Input<Object>[] inputs = getInputs(statements, builder, compiler);
        for (int i = 0; i < statements.length; i++) keys[i] = statements[i].key();
        return METHOD.of(CommandStatement.class, Boolean.class, "execute", Context.class, String.class, String[].class,
                Executable[].class)
            .getStatic(LOAD_VAR.object(1), CONSTANT.of(statement.input()),
                compiler.compileInputArray(keys, builder),
                ARRAY.of(Executable.class, inputs));
    }

    static Instruction.Input<Object>[] getInputs(InterpolationStatement[] statements, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Instruction.Input<Object>[] inputs = new Instruction.Input[statements.length];
        for (int i = 0; i < statements.length; i++) {
            inputs[i] = METHOD
                .metafactory(compiler.compileFunction(statements[i].statement(), builder),
                    Executable.class, "execute", builder)
                .invoke(LOAD_VAR.object(0));
        }
        return inputs;
    }

}

