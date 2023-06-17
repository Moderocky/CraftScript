package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.statement.PlusStatement;

public class PlusParser extends BinaryParser {

    public PlusParser() {
        super("+", PlusStatement::new);
    }

}
