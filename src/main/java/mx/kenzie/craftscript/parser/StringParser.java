package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.StringStatement;

import java.util.ArrayList;
import java.util.List;

public class StringParser extends BasicParser {

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        if (!input.startsWith("\"")) return false;
        if (!input.endsWith("\"")) return false;
        return input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"';
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        if (input.indexOf('}') > input.indexOf('{')) {
            int start = 0;
            final List<InterpolationStatement> list = new ArrayList<>();
            do {
                final int open = input.indexOf('{', start), close = input.indexOf('}', open);
                if (open < 0 || close < 0) break;
                final Statement<?> statement = parent.parse(input.substring(open, close + 1));
                if (statement instanceof InterpolationStatement interpolation) list.add(interpolation);
            } while (true);
            return new StringStatement(input.substring(1, input.length() - 1),
                list.toArray(new InterpolationStatement[0]));
        } else return new StringStatement(input.substring(1, input.length() - 1));
    }

}