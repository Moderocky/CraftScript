package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptParser;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.StringStatement;

import java.util.ArrayList;
import java.util.List;

public class StringParser extends BasicParser {

    public static InterpolationStatement[] interpolations(String input, ScriptParser parser) {
        int start;
        if (input.indexOf('}') > input.indexOf('{')) {
            start = 0;
            final List<InterpolationStatement> list = new ArrayList<>();
            do {
                final int open = input.indexOf('{', start), close = input.indexOf('}', open);
                if (open < 0 || close < 0) break;
                final String value = input.substring(open, close + 1);
                start = close;
                final Statement<?> statement = parser.parse(value);
                if (statement instanceof InterpolationStatement interpolation) list.add(interpolation);
            } while (true);
            return list.toArray(new InterpolationStatement[0]);
        } else return new InterpolationStatement[0];
    }

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        return input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"';
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        final InterpolationStatement[] statements = interpolations(input, parent);
        return new StringStatement(input.substring(1, input.length() - 1), statements);
    }

}
