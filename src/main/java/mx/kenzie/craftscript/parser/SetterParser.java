package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.SetterStatement;
import mx.kenzie.craftscript.statement.Statement;

public class SetterParser extends BasicParser {

    private Statement<?> source;
    private Statement<?> value;
    private String property;

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        if (!input.endsWith("]")) return false;
        final int index = this.openingBracket();
        if (index < 0) return false;
        final String label = input.substring(0, index).trim();
        final String string = input.substring(index + 1, input.length() - 1).trim();
        if (label.isEmpty()) return false;
        if (string.isEmpty()) return false;
        final int equals = string.indexOf('=');
        if (equals < 1) return false;
        this.property = string.substring(0, equals).trim();
        if (property.isEmpty()) return false;
        final String value = string.substring(equals + 1).trim();
        if (value.isEmpty()) return false;
        this.source = parent.parse(label);
        if (source == null) return false;
        this.value = parent.parse(value);
        return this.value != null;
    }

    private int openingBracket() {
        int start = input.length() - 2;
        do {
            final int open = input.lastIndexOf('[', start);
            final int close = input.lastIndexOf(']', start);
            if (close < 1) return open;
            else if (close >= open) start = open - 1;
            else return open;
        } while (start > 0);
        return -1;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new SetterStatement(source, property, value);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.source = null;
        this.value = null;
        this.property = null;
    }

}
