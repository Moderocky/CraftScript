package mx.kenzie.craftscript.compiler;

import mx.kenzie.foundation.Loader;

public class TestClassLoader extends ClassLoader implements Loader {

    protected TestClassLoader() {
        this(Loader.class.getClassLoader());
    }

    protected TestClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> loadClass(String name, byte[] bytecode) {
        return super.defineClass(name, bytecode, 0, bytecode.length);
    }

}
