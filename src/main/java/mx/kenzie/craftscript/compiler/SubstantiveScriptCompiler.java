package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.*;
import mx.kenzie.craftscript.utility.Comparator;
import mx.kenzie.foundation.Modifier;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.instruction.Instruction;

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
    }

    protected final Map<Class<?>, ElementCompiler<?>> compilers;

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
    protected Instruction.Input<Object> compileInput(Object object, PreClass builder) {
        if (object instanceof Object[] statements) return this.compileInputArray(statements, builder);
        if (object instanceof Comparator comparator) return this.compileComparator(comparator, builder);
        if (!(object instanceof Statement<?> statement)) return this.boxPrimitive(object);
        else throw new ScriptCompileError("Unable to compile '" + statement.stringify() + "'.");
    }

    @SuppressWarnings("unchecked")
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

    @Override
    public boolean compile(MultiStatement<Object> script, PreClass builder) {
        final PreMethod method = builder.add(new PreMethod(Type.of(Object.class), "execute", Context.class));
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
        return true;
    }

}
