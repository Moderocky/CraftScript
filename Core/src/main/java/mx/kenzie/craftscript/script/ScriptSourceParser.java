package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Warning;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

public interface ScriptSourceParser extends ElementParser {

    Statement<?> parseLine() throws IOException;

    int getLine();

    void incrementLine();

    Iterator<Parser> parsers();

    String readLine() throws IOException;

    boolean checkEmpty(String line);

    void warn(String text);

    Collection<Warning> warnings();

    void register(Supplier<Parser> parser);

}
