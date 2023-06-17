package mx.kenzie.craftscript.script;

import java.io.IOException;
import java.io.InputStream;

public interface ScriptLoader extends ScriptParser {

    Script load(String name, InputStream stream) throws IOException;

}
