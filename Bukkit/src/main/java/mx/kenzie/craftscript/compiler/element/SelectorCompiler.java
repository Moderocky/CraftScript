package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.SelectorStatement;
import mx.kenzie.foundation.instruction.CallMethod;

import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class SelectorCompiler extends InterpolatedContentCompiler<SelectorStatement> implements ElementCompiler<SelectorStatement> {


    @Override
    protected CallMethod.Stub getMethod() {
        return METHOD.of(SelectorStatement.class, Object.class, "execute", Context.class, String.class, Object[].class);
    }

    @Override
    protected Object[] getParts(SelectorStatement statement) {
        return statement.parts();
    }

    @Override
    protected String getBasic(SelectorStatement statement) {
        return statement.text();
    }

}

