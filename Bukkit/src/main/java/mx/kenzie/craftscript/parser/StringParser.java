package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptParser;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.LiteralStringStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.StringStatement;
import mx.kenzie.craftscript.utility.Interpolator;

import java.util.ArrayList;
import java.util.List;

public class StringParser extends BasicParser {

    public static InterpolationStatement[] interpolations(String input, ScriptParser parser) {
        int start;
        if (input.indexOf('}') > input.indexOf('{')) {
            start = 0;
            final List<InterpolationStatement> list = new ArrayList<>();
            do {
                int open = input.indexOf('{', start), close = input.indexOf('}', open);
                if (open < 0) break;
                do {
                    if (close < 0) break;
                    final String value = input.substring(open, close + 1);
                    final Statement<?> statement = parser.parse(value);
                    if (statement instanceof InterpolationStatement interpolation) {
                        list.add(interpolation);
                        break;
                    }
                    close = input.indexOf('}', close + 1);
                } while (close < input.length());
                start = close;
            } while (true);
            return list.toArray(new InterpolationStatement[0]);
        } else return new InterpolationStatement[0];
    }

    @Override
    public boolean matches() {
        if (input.length() < 2) return false;
        return input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"' && this.insideHasNoBreaks();
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        final String content = input.substring(1, input.length() - 1);
        final Object[] parts = new Interpolator(content, parent).interpolations();
        if (parts.length == 0 || (parts.length == 1 && parts[0] instanceof String))
            return new LiteralStringStatement(content);
        return new StringStatement(content, parts);
    }

    private boolean insideHasNoBreaks() {
        if (input.length() == 2) return true;
        final String inside = input.substring(1, input.length() - 1);
        int quote = inside.indexOf('"');
        if (quote < 0) return true;
        if (quote == 0) return false;
        do {
            if (inside.charAt(quote - 1) != '\\') return false;
            quote = inside.indexOf('"', quote + 1);
        } while (quote > 0 && quote < inside.length());
        return true;
    }

}
