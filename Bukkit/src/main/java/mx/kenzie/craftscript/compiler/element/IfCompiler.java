package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.IfStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class IfCompiler implements ElementCompiler<IfStatement> {

    @Override
    public Instruction.Input<?> compile(IfStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Instruction.Input<?> then = compiler.compileStatement(statement.then(), method, builder), check;
        check = compiler.compileStatement(statement.check(), method, builder);
        return visitor -> {
            final Label end = new Label();
            METHOD.of(IfStatement.class, boolean.class, "value", Object.class)
                .getStatic(check).write(visitor);
            visitor.visitInsn(Opcodes.DUP);
            visitor.visitJumpInsn(Opcodes.IFEQ, end);
            then.write(visitor);
            visitor.visitInsn(Opcodes.POP);
            visitor.visitLabel(end);
            METHOD.of(Boolean.class, Boolean.class, "valueOf", boolean.class).getStatic().write(visitor);
        };
    }

}

