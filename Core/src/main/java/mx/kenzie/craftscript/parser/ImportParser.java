package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.AnonymousParser;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ImportStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.VariableHelper;
import mx.kenzie.craftscript.variable.VariableContainer;

import java.util.ArrayList;
import java.util.List;

public class ImportParser extends BasicParser {

    private List<ImportStatement.Import> imports;

    @Override
    public boolean matches() {
        if (!input.startsWith("import")) return false;
        if (!input.endsWith("]")) return false;
        if (input.length() < 7) return false;
        final int start = input.indexOf('['), end = input.lastIndexOf(']');
        if (start < 0 || end < 0) return false;
        if (start > 7 && !input.substring(7, start).isBlank()) return false;
        final String string = input.substring(start + 1, end).trim();
        this.imports = new ArrayList<>();
        if (string.isEmpty()) return true;
        final String[] names = string.split(" *, *");
        final AnonymousParser parser = AnonymousParser.of(parent, FromParser::new, VariableParser::new);
        for (String name : names) {
            final Statement<?> statement = parser.parse(name.trim());
            if (statement == null) return false;
            this.imports.add(ImportStatement.of(statement));
        }
        return true;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        for (ImportStatement.Import anImport : imports) {
            if (anImport.name().equals("*")) continue;
            VariableHelper.instance().assign(anImport.name(), Object.class);
        }
        return new ImportStatement(imports.toArray(new ImportStatement.Import[0]));
    }

    @Override
    public void close() throws ScriptError {
        this.imports = null;
        super.close();
    }

}

class FromParser extends BasicParser {

    Statement<?> source;
    String target;

    @Override
    public boolean matches() {
        final int from = input.indexOf("from");
        if (from < 2) return false;
        if (input.length() < 8) return false;
        final String start = input.substring(0, from).trim(), end = input.substring(from + 4).trim();
        if (start.isEmpty() || end.isEmpty()) return false;
        if (!(start.equals("*") || VariableContainer.VAR_NAME.matcher(start).matches())) return false;
        this.source = AnonymousParser.of(parent, VariableParser::new).parse(end);
        this.target = start;
        return true;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return ImportStatement.variable(source, target);
    }

    @Override
    public void close() throws ScriptError {
        this.source = null;
        this.target = null;
        super.close();
    }

}
