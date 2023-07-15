package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.ScriptParser;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class Interpolator {

    protected final String input;
    protected final ScriptParser parser;

    public Interpolator(String input, ScriptParser parser) {
        this.input = input;
        this.parser = parser;
    }

    public Object[] interpolations() {
        int start;
        if (input.indexOf('}') > input.indexOf('{')) {
            start = 0;
            final List<Object> list = new ArrayList<>();
            do {
                int open = input.indexOf('{', start), close = input.indexOf('}', open);
                if (open < 0) break;
                do {
                    if (close < 0) break;
                    final String before = input.substring(start, open);
                    final String value = input.substring(open, close + 1);
                    final Statement<?> statement = parser.parse(value);
                    if (statement instanceof InterpolationStatement interpolation) {
                        if (!before.isEmpty()) list.add(before);
                        list.add(interpolation);
                        break;
                    }
                    close = input.indexOf('}', close + 1);
                } while (close < input.length());
                if (close == -1) break;
                start = close + 1;
            } while (true);
            final String part = input.substring(start);
            if (!part.isEmpty()) list.add(part);
            return list.toArray();
        } else return new Object[]{input};
    }

}
