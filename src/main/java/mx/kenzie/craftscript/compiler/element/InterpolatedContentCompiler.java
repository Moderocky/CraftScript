package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.CallMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.*;

abstract class InterpolatedContentCompiler<Type extends Statement<?>> implements ElementCompiler<Type> {

    protected abstract CallMethod.Stub getMethod();

    protected abstract Object[] getParts(Type statement);

    protected abstract String getBasic(Type statement);

    @Override
    @SuppressWarnings("unchecked")
    public Instruction.Input<?> compile(Type statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Object[] parts = this.getParts(statement);
        final Instruction.Input<Object>[] inputs = new Instruction.Input[parts.length];
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] instanceof String string) inputs[i] = CONSTANT.of(string);
            else if (parts[i] instanceof Statement<?> part)
                inputs[i] = (Instruction.Input<Object>) compiler.compileStatement(part, method, builder);
        }
        return this.getMethod()
            .getStatic(LOAD_VAR.object(1), CONSTANT.of(this.getBasic(statement)), ARRAY.of(Object.class, inputs));
    }

}
