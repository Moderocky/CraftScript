package mx.kenzie.craftscript.script;

import java.io.IOException;
import java.io.InputStream;

public interface ScriptLoader extends ScriptParser {

    Script parse(String name, InputStream stream) throws IOException;

    AbstractScript parse(InputStream stream) throws IOException;

}
