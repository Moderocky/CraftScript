package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.core.SuperFunction;
import mx.kenzie.craftscript.statement.InvokeStatement;
import mx.kenzie.craftscript.utility.Bridge;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import mx.kenzie.foundation.Modifier;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.*;
import mx.kenzie.foundation.instruction.AccessField;
import mx.kenzie.foundation.instruction.CallConstructor;
import mx.kenzie.foundation.instruction.CallMethod;
import mx.kenzie.foundation.instruction.Instruction;

import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static mx.kenzie.foundation.Type.OBJECT;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class ProxyCompiler<Result> {

    protected final Class<?> superClass;
    protected final Class<?>[] interfaces;
    protected final TinyClassLoader loader;
    protected final Map<Integer, Class<?>> classes;
    private volatile int count;
    private Map<String, AccessField.Stub> fields;

    public ProxyCompiler(Class<?> superClass, Class<?>... interfaces) throws ProxyCompileException {
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.loader = new TinyClassLoader(this.getClass().getClassLoader());
        this.classes = new HashMap<>();
        this.verify();
    }

    public static <Type> ProxyCompiler<Type> of(Class<?> superClass, Class<?>... interfaces) {
        try {
            return new ProxyCompiler<>(superClass, interfaces);
        } catch (ProxyCompileException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static Class<?> getWrapperType(Class<?> type) {
        if (type == boolean.class) return Boolean.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == char.class) return Character.class;
        if (type == void.class) return Void.class;
        return type;
    }

    private static Class<?> getUnwrapperType(Class<?> type) {
        if (type == Boolean.class) return boolean.class;
        if (type == Byte.class) return byte.class;
        if (type == Short.class) return short.class;
        if (type == Integer.class) return int.class;
        if (type == Long.class) return long.class;
        if (type == Float.class) return float.class;
        if (type == Double.class) return double.class;
        if (type == Character.class) return char.class;
        if (type == Void.class) return void.class;
        return type;
    }

    protected static Instruction.Input<Object> boxPrimitive(Class<?> type, Input<Object> input) {
        final Class<?> wrapper = getWrapperType(type);
        if (wrapper == type) return CAST.object(input, type);
        return METHOD.of(wrapper, wrapper, "valueOf", type).getStatic(input);
    }

    protected static Instruction.Input<Object> unboxPrimitive(Class<?> type, Input<Object> input) {
        final Class<?> raw = getUnwrapperType(type);
        if (raw == type) return CAST.object(input, type);
        return METHOD.of(Bridge.class, raw, "unbox", type).getStatic(input);
    }

    protected static Instruction.Input<Object> boxPrimitive(Object object) {
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

    protected synchronized int nextIndex() {
        return ++count;
    }

    protected void verify() throws ProxyCompileException {
        try {
            superClass.getConstructor();
        } catch (NoSuchMethodException ex) {
            throw new ProxyCompileException("No extendable zero-args constructor was present in the supertype");
        } catch (NullPointerException ex) {
            throw new ProxyCompileException("Supertype was null. Use java.lang.Object instead");
        }
        for (Class<?> anInterface : interfaces) {
            if (!anInterface.isInterface())
                throw new ProxyCompileException(anInterface + " was not an interface.");
        }
    }

    private int identityCode(Collection<String> keys) {
        int result = 1;
        for (final String element : keys) result = 31 * result + element.hashCode();
        return result;
    }

    public void compile(VariableContainer container) {

    }

    public Result create(AbstractScript script) {
        final Context<PrintStream> context = Context.system();
        script.execute(context);
        return this.create(context.variables());
    }

    public Result create(VariableContainer container) {
        final int identity = this.identityCode(container.keySet());
        if (classes.containsKey(identity)) return this.create(classes.get(identity), container);
        final PreClass builder = new PreClass("mx.kenzie.craftscript.synthetic", "Proxy_" + this.nextIndex());
        this.fields = new HashMap<>();
        builder.setParent(mx.kenzie.foundation.Type.of(superClass));
        builder.addInterfaces(Type.array(interfaces));
        final PreMethod constructor = PreMethod.constructor(Modifier.PUBLIC, Map.class);
        builder.add(constructor);
        final Map<String, SuperFunction<?>> functions = new HashMap<>();
        //<editor-fold desc="Constructor" defaultstate="collapsed">
        constructor.line(SUPER.of(superClass).call(THIS));
        for (Map.Entry<String, Object> entry : container.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            final AccessField.Stub field = this.makeField(builder, key, value);
            final CallMethod.Stub get = METHOD.of(Map.class, "get", Object.class);
            this.fields.put(key, field);
            this.setField(constructor, field, get.get(LOAD_VAR.object(1), CONSTANT.of(key)));
            if (Wrapper.unwrap(value) instanceof SuperFunction<?> function) functions.put(key, function);
        }
        constructor.line(RETURN.none());
        //</editor-fold>
        this.addMethodsFrom(superClass, builder, functions);
        for (Class<?> anInterface : interfaces) this.addMethodsFrom(anInterface, builder, functions);
        final Class<?> finished = builder.load(loader);
        this.classes.put(identity, finished);
        return this.create(finished, container);
    }

    private void setField(PreMethod method, AccessField.Stub field, Input<Object> input) {
        final CallMethod.Stub unwrap = METHOD.of(Wrapper.class, "unwrap", Object.class);
        if (field.type().isPrimitive())
            method.line(field.set(THIS, unboxPrimitive(field.type().toClass(), unwrap.getStatic(input))));
        else method.line(field.set(THIS, CAST.object(unwrap.getStatic(input), field.type())));
    }

    private Input<Object> getField(AccessField.Stub field) {
        if (field.type().isPrimitive())
            return boxPrimitive(field.type().toClass(), field.get(THIS));
        return field.get(THIS);
    }

    private AccessField.Stub makeField(PreClass builder, String name, Object value) {
        try {
            final Field field = superClass.getField(name);
            if (value != null && !field.getType().isInstance(Wrapper.unwrap(value))) throw new Exception();
            return FIELD.of(superClass, name, field.getType());
        } catch (Exception failure) {
            builder.add(new PreField(Modifier.PUBLIC, OBJECT, name));
            return FIELD.of(builder, name, OBJECT);
        }
    }

    private void addMethodsFrom(Class<?> type, PreClass builder, Map<String, SuperFunction<?>> functions) {
        for (Method method : type.getMethods()) {
            if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) continue;
            if (java.lang.reflect.Modifier.isFinal(method.getModifiers())) continue;
            final String name = method.getName();
            if (!functions.containsKey(name)) continue;
            final PreMethod caller = new PreMethod(Modifier.PUBLIC, method.getReturnType(), name, method.getParameterTypes());
            final CallMethod.Stub execute = METHOD.of(InvokeStatement.class, Object.class, "execute", Context.class, SuperFunction.class, SuperFunction.Arguments.class);
            final CallMethod.Stub system = METHOD.of(Context.class, Context.class, "system");
            final AccessField.Stub field = this.fields.get(name);
            final CallConstructor.Stub arg = NEW.of(SuperFunction.Argument.class, String.class, Object.class);
            final CallConstructor.Stub args = NEW.of(SuperFunction.Arguments.class, SuperFunction.Argument[].class);
            final CallMethod.Stub wrap = METHOD.of(Wrapper.class, "of", Object.class);
            final CallMethod.Stub unwrap = METHOD.of(Wrapper.class, "unwrap", Object.class);
            final Parameter[] parameters = method.getParameters();
            final Input<Object>[] inputs = new Input[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                final Parameter parameter = parameters[i];
                inputs[i] = arg.make(CONSTANT.of(parameter.getName()), wrap.getStatic(boxPrimitive(parameter.getType(), LOAD_VAR.object(i + 1))));
            }
            if (method.getReturnType() == void.class) {
                caller.line(execute.callStatic(system.getStatic(), CAST.object(field.get(THIS), SuperFunction.class), args.make(ARRAY.of(SuperFunction.Argument.class, inputs))));
                caller.line(RETURN.none());
            } else {
                final Input<Object> result = execute.getStatic(system.getStatic(), CAST.object(field.get(THIS), SuperFunction.class), args.make(ARRAY.of(SuperFunction.Argument.class, inputs)));
                caller.line(RETURN.object(unboxPrimitive(method.getReturnType(), unwrap.getStatic(result))));
            }
            builder.add(caller);
        }
    }

    protected Result create(Class<?> type, VariableContainer container) {
        try {
            final Constructor<Result> constructor = (Constructor<Result>) type.getConstructor(Map.class);
            return constructor.newInstance(container);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException
                 | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
