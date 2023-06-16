package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.statement.Statement;

public interface ScriptParser {

    Statement<?> parse(String line);

    int getLine();

}
