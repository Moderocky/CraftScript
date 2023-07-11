package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.statement.MultiStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.UnloadedClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface ScriptCompiler {

    boolean compileLine(Statement<?> statement, PreMethod method, PreClass builder);

    boolean compile(MultiStatement<Object> script, PreClass builder);

    byte[] compile(AbstractScript script);

    default UnloadedClass prepare(AbstractScript script) {
        return new UnloadedClass(Type.of("script", script.name().substring(0, script.name().indexOf('.'))),
            this.compile(script));
    }

    default void compile(AbstractScript script, OutputStream stream)
        throws IOException {
        final byte[] bytes = this.compile(script);
        stream.write(bytes);
        stream.flush();
    }

    default void compile(AbstractScript script, File file) {
        if (!file.exists() || !file.isFile()) throw new ScriptCompileError("File does not exist.");
        try (final FileOutputStream stream = new FileOutputStream(file)) {
            this.compile(script, stream);
        } catch (IOException ex) {
            throw new ScriptCompileError("Unable to write to file '" + file.getName() + "'.", ex);
        }
    }

}
