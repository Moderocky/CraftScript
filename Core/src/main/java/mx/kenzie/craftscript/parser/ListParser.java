package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ListStatement;
import mx.kenzie.craftscript.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class ListParser extends BasicParser {

    private List<Statement<?>> list = new ArrayList<>();

    @Override
    public boolean matches() {
        if (!input.startsWith("[")) return false;
        if (!input.endsWith("]")) return false;
        if (input.length() == 2 || input.substring(1, input.length() - 1).isBlank()) return true;
        int begin = 1;
        do {
            int comma, start = begin;
            do {
                comma = input.indexOf(',', start);
                if (comma < 1) comma = input.length() - 1;
                final String part = input.substring(begin, comma).trim();
                if (part.isEmpty()) return false;
                final Statement<?> element = parent.parse(part);
                if (begin < input.length() - 1 && element == null) continue;
                else if (element == null) return false;
                this.list.add(element);
                break;
            } while (true);
            begin = comma + 1;
        } while (begin < input.length() - 1);
        return true;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new ListStatement(list.toArray(new Statement[0]));
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.list = new ArrayList<>();
    }

}
