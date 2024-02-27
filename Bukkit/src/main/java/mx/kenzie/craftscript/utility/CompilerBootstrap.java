package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.compiler.ScriptCompileError;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.Type;
import org.objectweb.asm.Handle;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

public interface CompilerBootstrap {

    static Object setVariable(Context context, String name, Object value) {
        context.variables().put(name, value);
        return value;
    }

    static Object getVariable(Context context, String name) {
        return context.variables().get(name);
    }

    private static Handle getHandle(final PreMethod method) {
        final int code;
        if (Modifier.isStatic(method.getModifiers())) code = H_INVOKESTATIC;
        else if (Modifier.isAbstract(method.getModifiers())) code = H_INVOKEINTERFACE;
        else if (Modifier.isPrivate(method.getModifiers())) code = H_INVOKESPECIAL;
        else code = H_INVOKEVIRTUAL;
        return new Handle(code, method.getOwner().internalName(), method.name(),
            getDescriptor(method.returnType(), method.getParameters()),
            code == H_INVOKEINTERFACE);
    }

    private static Handle getHandle(final Method method) {
        final int code;
        if (Modifier.isStatic(method.getModifiers())) code = H_INVOKESTATIC;
        else if (Modifier.isAbstract(method.getModifiers())) code = H_INVOKEINTERFACE;
        else if (Modifier.isPrivate(method.getModifiers())) code = H_INVOKESPECIAL;
        else code = H_INVOKEVIRTUAL;
        return new Handle(code, Type.of(method.getDeclaringClass()).internalName(), method.getName(),
            getDescriptor(Type.of(method.getReturnType()), Type.array(method.getParameterTypes())),
            code == H_INVOKEINTERFACE);
    }

    private static String getDescriptor(final Type ret, final Type... params) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (final Type type : params) builder.append(type.descriptorString());
        builder
            .append(")")
            .append(ret.descriptorString());
        return builder.toString();
    }


    static Handle getBootstrap(final boolean isDynamic, final boolean isPrivate) {
        final String target;
        if (isPrivate) target = isDynamic ? "bootstrapPrivateDynamic" : "bootstrapPrivate";
        else target = isDynamic ? "bootstrapDynamic" : "bootstrap";
        final Class<?>[] array = new Class[] {MethodHandles.Lookup.class, String.class, MethodType.class, Class.class};
        final Method method;
        try {
            method = CompilerBootstrap.class.getMethod(target, array);
        } catch (Throwable ex) {
            throw new ScriptCompileError("Bootstrap error.", ex);
        }
        return CompilerBootstrap.getHandle(method);
    }

    private static Class<?> getClass(String name) throws ClassNotFoundException {
        return switch (name) {
            case "boolean" -> boolean.class;
            case "char" -> char.class;
            case "void" -> void.class;
            case "byte" -> byte.class;
            case "short" -> short.class;
            case "int" -> int.class;
            case "long" -> long.class;
            case "float" -> float.class;
            case "double" -> double.class;
            default -> Class.forName(name);
        };
    }


    static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = caller.findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapPrivate(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller).findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapDynamic(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle;
        final MethodType end = type.dropParameterTypes(0, 1);
        handle = caller.findVirtual(owner, name, end);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapPrivateDynamic(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle;
        final MethodType end = type.dropParameterTypes(0, 1);
        handle = MethodHandles.privateLookupIn(owner, caller).findVirtual(owner, name, end);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapStaticFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapStaticFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapPrivateStaticFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapPrivateStaticFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = caller.findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapPrivateFieldSetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.parameterType(0))
            .toMethodHandle(VarHandle.AccessMode.SET);
        return new ConstantCallSite(handle);
    }


    static CallSite bootstrapPrivateFieldGetter(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = MethodHandles.privateLookupIn(owner, caller)
            .findStaticVarHandle(owner, name, type.returnType())
            .toMethodHandle(VarHandle.AccessMode.GET);
        return new ConstantCallSite(handle);
    }

}
