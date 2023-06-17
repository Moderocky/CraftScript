package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.statement.EqualsStatement;

public class EqualsParser extends OperatorParser {

    public EqualsParser() {
        super("==", EqualsStatement::new);
    }

}
