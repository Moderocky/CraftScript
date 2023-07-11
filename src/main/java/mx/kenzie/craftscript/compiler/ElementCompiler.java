package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.compiler.element.*;
import mx.kenzie.craftscript.statement.*;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.instruction.Instruction;

import static mx.kenzie.foundation.instruction.Instruction.CONSTANT;
import static mx.kenzie.foundation.instruction.Instruction.Input;

public interface ElementCompiler<Type extends Statement<?>> {

    ElementCompiler<LiteralStatement> LITERAL = (statement, method, builder, compiler) ->
        compiler.compileConstant(statement.value(), method, builder);
    Inline<LiteralStringStatement> LITERAL_STRING = statement -> CONSTANT.of(statement.value());
    Inline<NullStatement> NULL = statement -> Instruction.NULL;
    ElementCompiler<VariableAssignmentStatement> VARIABLE_ASSIGNMENT = new VariableAssignmentCompiler();
    ElementCompiler<VariableStatement> VARIABLE = new VariableCompiler();
    ElementCompiler<AssertStatement> ASSERT = new AssertCompiler();
    ElementCompiler<BlockStatement> BLOCK = new BlockCompiler();
    ElementCompiler<CompareStatement> COMPARE = new CompareCompiler();
    ElementCompiler<ImportStatement> IMPORT = new ImportCompiler();
    ElementCompiler<RequireStatement> REQUIRE = new RequireCompiler();

    Instruction.Input<?> compile(Type type, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler);

    interface Inline<Type extends Statement<?>> extends ElementCompiler<Type> {

        @Override
        default Input<?> compile(Type type, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
            return this.compile(type);
        }

        Input<?> compile(Type type);

    }

}
