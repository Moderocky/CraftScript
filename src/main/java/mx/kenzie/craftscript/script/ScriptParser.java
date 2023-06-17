package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.statement.Statement;

import java.io.IOException;

public interface ScriptParser {

    Statement<?> parseLine() throws IOException;

    Statement<?> parse(String line);

    int getLine();

}
