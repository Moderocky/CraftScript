package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.statement.Statement;

public interface ElementParser {

    Statement<?> parse(String element);

}
