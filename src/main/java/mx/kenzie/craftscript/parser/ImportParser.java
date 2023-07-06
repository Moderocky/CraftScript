package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ImportStatement;
import mx.kenzie.craftscript.statement.Statement;

public class ImportParser extends BasicParser {

    private String[] names;

    @Override
    public boolean matches() {
        if (!input.startsWith("import")) return false;
        if (!input.endsWith("]")) return false;
        if (input.length() < 7) return false;
        final int start = input.indexOf('['), end = input.lastIndexOf(']');
        if (start < 0 || end < 0) return false;
        if (start > 7 && !input.substring(7, start).isBlank()) return false;
        final String string = input.substring(start + 1, end).trim();
        this.names = new String[0];
        if (string.isEmpty()) return true;
        this.names = string.split(" *, *");
        return true;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new ImportStatement(names);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.names = null;
    }

}
