package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.statement.MultiStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Comparator;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.foundation.*;
import mx.kenzie.foundation.instruction.Instruction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

import static mx.kenzie.foundation.instruction.Instruction.*;

public class SimpleScriptCompiler implements ScriptCompiler {

    protected Instruction.Input<Object> boxPrimitive(Object object) {
        if (object instanceof Boolean) return METHOD
            .of(Boolean.class, Boolean.class, "valueOf", boolean.class)
            .getStatic(CONSTANT.of(object));
        if (object instanceof Integer) return METHOD
            .of(Integer.class, Integer.class, "valueOf", int.class)
            .getStatic(CONSTANT.of(object));
        if (object instanceof Number number) return METHOD
            .of(Double.class, Double.class, "valueOf", double.class)
            .getStatic(CONSTANT.of(number.doubleValue()));
        else return CONSTANT.of(object);
    }

    @SuppressWarnings("unchecked")
    protected Instruction.Input<Object> compileInputArray(Object[] objects, PreClass builder) {
        final Input<Object>[] inputs = new Input[objects.length];
        for (int i = 0; i < objects.length; i++) inputs[i] = this.compileInput(objects[i], builder);
        if (objects.getClass() == InterpolationStatement[].class) return ARRAY.of(InterpolationStatement.class, inputs);
        return ARRAY.of(Object.class, inputs);
    }

    protected Instruction.Input<Object> compileComparator(Comparator comparator, PreClass builder) {
        final String field = switch (comparator.toString()) {
            case "<" -> "LESS_THAN";
            case "<=" -> "LESS_EQUAL";
            case ">" -> "GREATER_THAN";
            case ">=" -> "GREATER_EQUAL";
            case "&" -> "AND";
            case "|" -> "OR";
            case "^" -> "XOR";
            case "!=" -> "NE";
            case "?" -> "ALT";
            default -> throw new IllegalStateException("Unexpected value: " + comparator);
        };
        return FIELD.of(Comparator.class, field, Comparator.class).get();
    }

    @SuppressWarnings("unchecked")
    protected <SimpleStatement extends Record & Statement<?>> Instruction.Input<Object> makeStatement(Statement<?> thing, PreClass builder) {
        final SimpleStatement statement = (SimpleStatement) thing;
        final Class<?> type = statement.getClass();
        final Class<?>[] parameters = new Class[type.getRecordComponents().length];
        final List<Input<Object>> arguments = new ArrayList<>(parameters.length);
        int i = 0;
        for (final RecordComponent component : type.getRecordComponents()) {
            parameters[i++] = component.getType();
            try {
                final Object value = component.getAccessor().invoke(statement);
                arguments.add(this.compileInput(value, builder));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new ScriptCompileError("Unable to access record.", e);
            }
        }
        return NEW.of(type, parameters).make(arguments.toArray(new Input[0]));
    }

    protected Instruction.Input<Object> compileInput(Object object, PreClass builder) {
        if (object instanceof Object[] statements) return this.compileInputArray(statements, builder);
        if (object instanceof Comparator comparator) return this.compileComparator(comparator, builder);
        if (!(object instanceof Statement<?> statement)) return this.boxPrimitive(object);
        else if (statement instanceof AbstractScript)
            throw new ScriptCompileError("Tried to compile a script inside a script.");
//        else if (statement instanceof LiteralStatement literal) return CONSTANT.of(literal.value());
        else if (statement instanceof LineStatement line) {
            final Input<Object> input = this.compileInput(line.statement(), builder);
            return visitor -> {
                this.writeLineNumber(line.line(), builder);
                input.write(visitor);
            };
        } else if (statement instanceof Record) return this.makeStatement(statement, builder);
        else throw new ScriptCompileError("Unable to compile '" + statement.stringify() + "'.");
    }

    protected Instruction.Input<Object> compileStatement(Statement<?> statement, PreClass builder) {
        return METHOD // we make a new XStatement(...) then we execute(context)... it
            .of(true, Executable.class, Object.class, "execute", Context.class)
            .get(this.compileInput(statement, builder), LOAD_VAR.object(1));
    }

    protected Base writeLineNumber(int line, PreClass builder) {
        return METHOD
            .of(builder, void.class, "line", Context.class, int.class)
            .call(LOAD_VAR.self(), LOAD_VAR.object(1), CONSTANT.of(line));
    }

    protected boolean compileLine(LineStatement line, PreMethod method, PreClass builder) {
        method.line(this.writeLineNumber(line.line(), builder));
        return this.compileLine(line.statement(), method, builder);
    }

    @Override
    public boolean compileLine(Statement<?> statement, PreMethod method, PreClass builder) {
        if (statement instanceof LineStatement line) return this.compileLine(line, method, builder);
        method.line(STORE_VAR.object(2, this.compileStatement(statement, builder)));
        return true;
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

    @Override
    public byte[] compile(AbstractScript script) {
        return this.prepare(script).bytecode();
    }

    protected void addMeta(AbstractScript script, PreClass builder) {
        final PreMethod constructor = builder.add(PreMethod.constructor(Modifier.PUBLIC));
        constructor.line(SUPER
            .of(CompiledScript.class, String.class)
            .call(LOAD_VAR.self(), CONSTANT.of(script.name()))
        );
        constructor.line(RETURN.none());
    }

    @Override
    public UnloadedClass prepare(AbstractScript script) {
        final PreClass builder = new PreClass("script", script.name().substring(0, script.name().indexOf('.')));
        builder.setParent(Type.of(CompiledScript.class));
        builder.addInterfaces(Type.of(Statement.class));
        this.addMeta(script, builder);
        this.compile(script, builder);
        return builder.compile();
    }

}
