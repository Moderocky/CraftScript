package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.RunStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class RunCompiler implements ElementCompiler<RunStatement> {

    public static Input<?> compile(Input<?> function, Input<?> data, PreClass builder) {
        return visitor -> {
            METHOD.of(RunStatement.class, Object.class, "execute", Context.class, Object.class, Object.class)
                .getStatic(LOAD_VAR.object(1), function, data)
                .write(visitor);
            METHOD
                .of(builder, void.class, "prepare", Context.class)
                .call(LOAD_VAR.self(), LOAD_VAR.object(1))
                .write(visitor);
        };
    }

    @Override
    public Instruction.Input<?> compile(RunStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Input<?> function = compiler.compileStatement(statement.statement(), method, builder), data;
        if (statement.data() == null) data = Instruction.NULL;
        else data = compiler.compileStatement(statement.data(), method, builder);
        return compile(function, data, builder);
    }

}

