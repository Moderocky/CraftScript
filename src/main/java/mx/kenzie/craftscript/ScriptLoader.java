package mx.kenzie.craftscript;

import mx.kenzie.craftscript.parser.ScriptParser;

import java.io.IOException;
import java.io.InputStream;

public interface ScriptLoader extends ScriptParser {

    Script load(String name, InputStream stream) throws IOException;

}
