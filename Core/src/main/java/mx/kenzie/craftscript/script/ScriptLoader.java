package mx.kenzie.craftscript.script;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public interface ScriptLoader extends ScriptSourceParser {

    Script parse(String name, InputStream stream) throws IOException;

    AbstractScript parse(InputStream stream) throws IOException;

    default AbstractScript parseScript(String string) {
        try {
            return this.parse(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
