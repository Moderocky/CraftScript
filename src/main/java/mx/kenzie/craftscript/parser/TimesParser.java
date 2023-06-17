package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.statement.TimesStatement;

public class TimesParser extends BinaryParser {

    public TimesParser() {
        super("*", TimesStatement::new);
    }

}
