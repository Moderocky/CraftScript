package mx.kenzie.craftscript.compiler;

import mx.kenzie.foundation.Loader;

public class TinyClassLoader extends ClassLoader implements Loader {

    public TinyClassLoader() {
        this(Loader.class.getClassLoader());
    }

    protected TinyClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> loadClass(String name, byte[] bytecode) {
        return super.defineClass(name, bytecode, 0, bytecode.length);
    }

}
