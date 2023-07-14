package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.StringStatement;
import mx.kenzie.craftscript.utility.Bridge;
import mx.kenzie.foundation.instruction.CallMethod;

import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class StringCompiler extends InterpolatedContentCompiler<StringStatement> implements ElementCompiler<StringStatement> {

    @Override
    protected CallMethod.Stub getMethod() {
        return METHOD.of(StringStatement.class, String.class, "execute", Context.class, String.class, Object[].class);
    }

    @Override
    protected Object[] getParts(StringStatement statement) {
        return statement.parts();
    }

    @Override
    protected String getBasic(StringStatement statement) {
        return statement.value();
    }

}

