package mx.kenzie.craftscript.compiler.element;

import mx.kenzie.craftscript.compiler.ElementCompiler;
import mx.kenzie.craftscript.compiler.SubstantiveScriptCompiler;
import mx.kenzie.craftscript.statement.IfElseStatement;
import mx.kenzie.craftscript.statement.IfStatement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static mx.kenzie.foundation.instruction.Instruction.METHOD;

public class IfElseCompiler implements ElementCompiler<IfElseStatement> {

    @Override
    public Instruction.Input<?> compile(IfElseStatement statement, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
        final Instruction.Input<?> either = compiler.compileStatement(statement.choice().either(), method, builder);
        final Instruction.Input<?> or = compiler.compileStatement(statement.choice().or(), method, builder);
        return visitor -> {
            final Label otherwise = new Label(), end = new Label();
            METHOD.of(IfStatement.class, boolean.class, "value", Object.class)
                .getStatic(compiler.compileStatement(statement.check(), method, builder)).write(visitor);
            visitor.visitInsn(Opcodes.DUP);
            visitor.visitJumpInsn(Opcodes.IFEQ, otherwise);
            either.write(visitor);
            visitor.visitInsn(Opcodes.POP);
            visitor.visitJumpInsn(Opcodes.GOTO, end);
            visitor.visitLabel(otherwise);
            or.write(visitor);
            visitor.visitInsn(Opcodes.POP);
            visitor.visitLabel(end);
            METHOD.of(Boolean.class, Boolean.class, "valueOf", boolean.class).getStatic().write(visitor);
        };
    }

}

