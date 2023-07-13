package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.*;
import mx.kenzie.craftscript.utility.Comparator;
import mx.kenzie.foundation.*;
import mx.kenzie.foundation.instruction.Instruction;
import org.objectweb.asm.ClassWriter;

import java.util.HashMap;
import java.util.Map;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class SubstantiveScriptCompiler extends SimpleScriptCompiler {

    protected static final Map<Class<?>, ElementCompiler<?>> COMPILER_MAP = new HashMap<>();

    static {
        register(NullStatement.class, ElementCompiler.NULL);
        register(LiteralStatement.class, ElementCompiler.LITERAL);
        register(LiteralStringStatement.class, ElementCompiler.LITERAL_STRING);
        register(VariableAssignmentStatement.class, ElementCompiler.VARIABLE_ASSIGNMENT);
        register(VariableStatement.class, ElementCompiler.VARIABLE);
        register(AssertStatement.class, ElementCompiler.ASSERT);
        register(BlockStatement.class, ElementCompiler.BLOCK);
        register(CompareStatement.class, ElementCompiler.COMPARE);
        register(ImportStatement.class, ElementCompiler.IMPORT);
        register(RequireStatement.class, ElementCompiler.REQUIRE);
        register(InvertStatement.class, ElementCompiler.INVERT);
        register(ListStatement.class, ElementCompiler.LIST);
        register(ScriptStatement.class, ElementCompiler.SCRIPT);
        register(FunctionStatement.class, ElementCompiler.FUNCTION);
        register(RunStatement.class, ElementCompiler.RUN);
        register(IfStatement.class, ElementCompiler.IF);
        register(IfElseStatement.class, ElementCompiler.IF_ELSE);
        register(CommandStatement.class, ElementCompiler.COMMAND);
        register(StringStatement.class, ElementCompiler.STRING);
        register(ListenerStatement.class, ElementCompiler.LISTENER);
        register(InterpolationStatement.class, ElementCompiler.INTERPOLATION);
        register(MapStatement.class, ElementCompiler.MAP);
        register(StructStatement.class, ElementCompiler.STRUCT);
        register(SyntaxStatement.class, ElementCompiler.SYNTAX);
        register(KindStatement.class, ElementCompiler.KIND);
        register(LocalFunctionStatement.class, ElementCompiler.LOCAL_FUNCTION);
        register(LocalKeywordStatement.class, ElementCompiler.LOCAL_KEYWORD);
        register(LocalSyntaxStatement.class, ElementCompiler.LOCAL_SYNTAX);
        register(SelectorStatement.class, ElementCompiler.SELECTOR);
        register(DoStatement.class, ElementCompiler.DO);
        register(ForStatement.class, ElementCompiler.FOR);
        register(GetterStatement.class, ElementCompiler.GETTER);
        register(SetterStatement.class, ElementCompiler.SETTER);
        register(WhileStatement.class, ElementCompiler.WHILE);
    }

    protected final Map<Class<?>, ElementCompiler<?>> compilers;
    protected int methodCounter;

    public SubstantiveScriptCompiler(Map<Class<?>, ElementCompiler<?>> compilers) {this.compilers = compilers;}

    public SubstantiveScriptCompiler() {
        this(new HashMap<>(COMPILER_MAP));
    }

    static <Type extends Statement<?>> void register(Class<Type> type, ElementCompiler<Type> compiler) {
        COMPILER_MAP.put(type, compiler);
    }

    @Override
    public boolean compileLine(Statement<?> statement, PreMethod method, PreClass builder) {
        if (statement instanceof LineStatement line) return this.compileLine(line, method, builder);
        method.line(STORE_VAR.object(2, this.compileStatement(statement, method, builder)));
        return true;
    }

    public Input<?> compileConstant(Object constant, PreMethod method, PreClass builder) {
        return this.compileInput(constant, builder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Instruction.Input<Object> compileInputArray(Object[] objects, PreClass builder) {
        final Input<Object>[] inputs = new Input[objects.length];
        for (int i = 0; i < objects.length; i++) inputs[i] = this.compileInput(objects[i], builder);
        return ARRAY.of(objects.getClass().getComponentType(), inputs);
    }

    @Override
    protected Instruction.Input<Object> compileInput(Object object, PreClass builder) {
        if (object instanceof Object[] statements) return this.compileInputArray(statements, builder);
        if (object instanceof Comparator comparator) return this.compileComparator(comparator, builder);
        if (!(object instanceof Statement<?> statement)) return this.boxPrimitive(object);
        else throw new ScriptCompileError("Unable to compile '" + statement.stringify() + "'.");
    }

    @SuppressWarnings({"unchecked", "TypeParameterHidesVisibleType"})
    public <Type extends Statement<?>> Input<?> compileStatement(Type statement, PreMethod method, PreClass builder) {
        final ElementCompiler<Type> compiler = (ElementCompiler<Type>) compilers.get(statement.getClass());
        if (compiler == null) throw new ScriptCompileError(
            "No element compiler found for '" + statement.getClass().getSimpleName() + "'.");
        return compiler.compile(statement, method, builder, this);
    }

    @Override
    protected Input<Object> compileStatement(Statement<?> statement, PreClass builder) {
        throw new ScriptCompileError("This method is not used.");
    }

    @SuppressWarnings("unchecked")
    public PreMethod compileFunction(Statement<?> statement, PreClass builder) {
        final PreMethod method = builder.add(
            new PreMethod(Type.of(Object.class), "function" + ++methodCounter, Context.class));
        method.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        method.line(METHOD
            .of(builder, void.class, "prepare", Context.class)
            .call(LOAD_VAR.self(), LOAD_VAR.object(1))
        );
        method.line(RETURN.object((Input<Object>) this.compileStatement(statement, method, builder)));
        return method;
    }

    @Override
    public PreMethod compile(MultiStatement<Object> script, PreClass builder) {
        final String name;
        if (script instanceof AbstractScript) name = "execute";
        else name = "block" + ++methodCounter;
        final PreMethod method = builder.add(new PreMethod(Type.of(Object.class), name, Context.class));
        method.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        method.line(METHOD
            .of(builder, void.class, "prepare", Context.class)
            .call(LOAD_VAR.self(), LOAD_VAR.object(1))
        );
        method.line(STORE_VAR.object(2, NULL)); // so we know it's ok to return 2
        for (final Statement<?> statement : script.statements()) {
            final boolean ok = this.compileLine(statement, method, builder);
            assert ok;
        }
        method.line(RETURN.object(LOAD_VAR.object(2)));
        return method;
    }

    @Override
    public UnloadedClass prepare(AbstractScript script) {
        final var builder = new PreClass("script", script.name().substring(0, script.name().indexOf('.'))) {

            @Override
            public byte[] bytecode() {
                ClassWriter writer = new ClassWriter(2);
                this.build(writer);
                return writer.toByteArray();
            }
        };
        builder.setParent(Type.of(CompiledScript.class));
        builder.addInterfaces(Type.of(Statement.class));
        this.addMeta(script, builder);
        this.compile(script, builder);
        return builder.compile();
    }

}
