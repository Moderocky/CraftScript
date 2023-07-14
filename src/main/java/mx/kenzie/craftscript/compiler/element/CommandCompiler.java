package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.CommandStatement;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.StringStatement;
import mx.kenzie.craftscript.utility.Bridge;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.CallMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class CommandCompiler extends InterpolatedContentCompiler<CommandStatement> implements ElementCompiler<CommandStatement> {

    @Override
    protected CallMethod.Stub getMethod() {
        return METHOD.of(CommandStatement.class, Boolean.class, "execute", Context.class, String.class, Object[].class);
    }

    @Override
    protected Object[] getParts(CommandStatement statement) {
        return statement.parts();
    }

    @Override
    protected String getBasic(CommandStatement statement) {
        return statement.input();
    }

}

