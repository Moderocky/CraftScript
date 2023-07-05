package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.Statement;

import java.io.IOException;
import java.util.Iterator;

public interface ScriptParser {

    Statement<?> parseLine() throws IOException;

    Statement<?> parse(String line);

    int getLine();

    void incrementLine();

    Iterator<Parser> parsers();

    String readLine() throws IOException;

    boolean checkEmpty(String line);

}
