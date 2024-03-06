package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.core.SuperFunction;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableStatement;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import mx.kenzie.foundation.*;
import mx.kenzie.foundation.instruction.CallMethod;
import mx.kenzie.foundation.instruction.Instruction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import static mx.kenzie.craftscript.compiler.ProxyCompiler.boxPrimitive;
import static mx.kenzie.craftscript.compiler.ProxyCompiler.unboxPrimitive;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class SuperFunctionCompiler {

    protected final Method method;

    public SuperFunctionCompiler(Method method) {
        this.method = method;
    }

    public boolean testCompatibility() {
        return true;
    }

    public SuperFunction<?> create(Loader loader, Object source) {
        final String type = Type.methodDescriptor(method.getReturnType(), method.getParameterTypes());
        final String name = method.getDeclaringClass().getSimpleName() + "_" + method.getName() + "_" + type.replaceAll("[();]", "").replaceAll("[/\\[.]+", "_");
        final PreClass builder = new PreClass("mx.kenzie.craftscript.synthetic", name + "Function");
        builder.setParent(Type.of(Object.class));
        builder.addInterfaces(Type.of(Executable.class));
        final PreField field = new PreField(Modifier.PUBLIC, method.getDeclaringClass(), "target");
        builder.add(field);
        final PreMethod constructor = PreMethod.constructor(Modifier.PUBLIC, Type.OBJECT);
        builder.add(constructor);
        constructor.line(SUPER.of(Type.OBJECT).call(THIS));
        constructor.line(field.set(THIS, CAST.object(LOAD_VAR.object(1), method.getDeclaringClass())));
        constructor.line(RETURN.none());
        final PreMethod execute = new PreMethod(Modifier.PUBLIC, Object.class, "execute", Context.class);
        builder.add(execute);
        final CallMethod.Stub stub = METHOD.of(method.getDeclaringClass(), method.getReturnType(), method.getName(), method.getParameterTypes());
        final Parameter[] parameters = method.getParameters();
        final Instruction.Input<?>[] inputs = new Input[parameters.length];
        final CallMethod.ConstantStub get = METHOD.of(Map.class, Object.class, "get", Object.class);
        final CallMethod.ConstantStub variables = METHOD.of(Context.class, VariableContainer.class, "variables");
        final CallMethod.Stub unwrap = METHOD.of(Wrapper.class, "unwrap", Object.class);
        final Statement<?>[] statements = new Statement[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            inputs[i] = unboxPrimitive(parameters[i].getType(), unwrap.getStatic(get.get(variables.get(LOAD_VAR.object(0)), CONSTANT.of(parameters[i].getName()))));
            statements[i] = new VariableStatement(parameters[i].getName());
        }
        execute.line(RETURN.object(boxPrimitive(method.getReturnType(), stub.get(field.get(THIS), inputs))));
        final Class<?> finished = builder.load(loader);
        try {
            final Executable<?> executable = (Executable<?>) finished.getConstructor(Object.class).newInstance(source);
            return new SuperFunction<>(executable, statements);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new ScriptError(e);
        }
    }

}
