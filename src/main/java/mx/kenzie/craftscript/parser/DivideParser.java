package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.statement.DivideStatement;

public class DivideParser extends BinaryParser {

    public DivideParser() {
        super("/", DivideStatement::new);
    }

}
