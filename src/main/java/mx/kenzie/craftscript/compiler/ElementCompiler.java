package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.compiler.element.ScriptCompiler;
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
    Inline<SyntaxStatement> SYNTAX = statement -> Instruction.NULL;
    ElementCompiler<VariableAssignmentStatement> VARIABLE_ASSIGNMENT = new VariableAssignmentCompiler();
    ElementCompiler<VariableStatement> VARIABLE = new VariableCompiler();
    ElementCompiler<AssertStatement> ASSERT = new AssertCompiler();
    ElementCompiler<BlockStatement> BLOCK = new BlockCompiler();
    ElementCompiler<CompareStatement> COMPARE = new CompareCompiler();
    ElementCompiler<ImportStatement> IMPORT = new ImportCompiler();
    ElementCompiler<RequireStatement> REQUIRE = new RequireCompiler();
    ElementCompiler<InvertStatement> INVERT = new InvertCompiler();
    ElementCompiler<ListStatement> LIST = new ListCompiler();
    ElementCompiler<ScriptStatement> SCRIPT = new ScriptCompiler();
    ElementCompiler<FunctionStatement> FUNCTION = new FunctionCompiler();
    ElementCompiler<RunStatement> RUN = new RunCompiler();
    ElementCompiler<IfStatement> IF = new IfCompiler();
    ElementCompiler<IfElseStatement> IF_ELSE = new IfElseCompiler();
    ElementCompiler<CommandStatement> COMMAND = new CommandCompiler();
    ElementCompiler<StringStatement> STRING = new StringCompiler();
    ElementCompiler<ListenerStatement> LISTENER = new ListenerCompiler();
    ElementCompiler<InterpolationStatement> INTERPOLATION = new InterpolationCompiler();
    ElementCompiler<MapStatement> MAP = new MapCompiler();
    ElementCompiler<StructStatement> STRUCT = new StructCompiler();
    ElementCompiler<KindStatement> KIND = new KindCompiler();
    ElementCompiler<LocalFunctionStatement> LOCAL_FUNCTION = new LocalFunctionCompiler();
    ElementCompiler<LocalKeywordStatement> LOCAL_KEYWORD = new LocalKeywordCompiler();
    ElementCompiler<LocalSyntaxStatement> LOCAL_SYNTAX = new LocalSyntaxCompiler();
    ElementCompiler<SelectorStatement> SELECTOR = new SelectorCompiler();
    ElementCompiler<DoStatement> DO = new DoCompiler();
    ElementCompiler<ForStatement> FOR = new ForCompiler();
    ElementCompiler<GetterStatement> GETTER = new GetterCompiler();
    ElementCompiler<SetterStatement> SETTER = new SetterCompiler();
    ElementCompiler<WhileStatement> WHILE = new WhileCompiler();

    Instruction.Input<?> compile(Type type, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler);

    interface Inline<Type extends Statement<?>> extends ElementCompiler<Type> {

        @Override
        default Input<?> compile(Type type, PreMethod method, PreClass builder, SubstantiveScriptCompiler compiler) {
            return this.compile(type);
        }

        Input<?> compile(Type type);

    }

}
