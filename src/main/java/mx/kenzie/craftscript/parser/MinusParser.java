package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.statement.MinusStatement;

public class MinusParser extends BinaryParser {

    public MinusParser() {
        super("-", MinusStatement::new);
    }

}
