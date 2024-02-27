/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.compiler.ScriptCompileError;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public class Metafactory {

    private static Method findTarget(MethodHandles.Lookup caller, String name, Class<?> owner, Class<?>... parameters) {
        final List<Method> varArgs = new ArrayList<>();
        {
            final Method[] methods = owner.getMethods();
            for (final Method method : methods) {
                if (!method.getName().equals(name)) continue;
                if (Arrays.equals(method.getParameterTypes(), parameters)) return method;
            }
            for (final Method method : methods) {
                if (!Modifier.isStatic(method.getModifiers())) continue; // can only hit statics for now
                if (!method.getName().equals(name)) continue;
                if (method.isVarArgs()) varArgs.add(method);
                if (parameters.length == method.getParameterCount()) return method;
            }
        }
        {
            final Method[] methods = owner.getDeclaredMethods();
            for (final Method method : methods) {
                if (!method.getName().equals(name)) continue;
                if (Arrays.equals(method.getParameterTypes(), parameters)) return method;
            }
            for (final Method method : methods) {
                if (!Modifier.isStatic(method.getModifiers())) continue; // can only hit statics for now
                if (!method.getName().equals(name)) continue;
                if (method.isVarArgs()) varArgs.add(method);
                if (parameters.length == method.getParameterCount()) return method;
            }
        }
        for (final Method method : varArgs) {
            final int check = method.getParameterCount() - 1;
            if (parameters.length < check) continue;
            if (Arrays.equals(method.getParameterTypes(), 0, check, parameters, 0, check)) return method;
        }
        for (final Method method : varArgs) {
            if (parameters.length < method.getParameterCount() - 1) continue;
            return method;
        }
        throw new ScriptCompileError("Unable to find function '" + name + Arrays.toString(parameters).replace('[', '(')
            .replace(']', ')') + "' from " + owner.getSimpleName());
    }

    public static CallSite lambda(MethodHandles.Lookup caller, String name, MethodType type, Class<?> owner)
        throws Exception {
        final MethodHandle handle = caller.findStatic(owner, name, type);
        return new ConstantCallSite(handle);
    }

}
