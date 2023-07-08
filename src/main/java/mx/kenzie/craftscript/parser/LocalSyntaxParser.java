package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.LocalSyntaxStatement;
import mx.kenzie.craftscript.statement.NullStatement;
import mx.kenzie.craftscript.statement.Statement;

import java.util.*;

public class LocalSyntaxParser extends BasicParser {

    protected final String pattern;
    protected final Element[] elements;
    protected final Statement<?> function;
    protected final int inputs;
    protected Map<String, Statement<?>> map;

    public LocalSyntaxParser(String pattern) {
        this(pattern, new NullStatement());
    }

    public LocalSyntaxParser(String pattern, Statement<?> function) {
        this.pattern = pattern;
        this.function = function;
        this.elements = tokenize(pattern);
        if (elements.length == 0) throw new ScriptError("Line " + this.line() + ": Syntax pattern was empty.");
        int count = 0;
        final Set<String> keys = new HashSet<>(elements.length);
        for (final Element element : elements) {
            if (!(element instanceof Input input)) continue;
            count++;
            if (keys.contains(input.value)) throw new ScriptError("Line " + this.line()
                + ": Duplicate input name '" + input.value + "' in custom syntax '" + pattern + "'");
            keys.add(input.value);
        }
        this.inputs = count;
    }

    public static Element[] tokenize(String pattern) {
        boolean mode = false;
        int start = 0;
        final List<Element> list = new ArrayList<>(8);
        do {
            final int percent = pattern.indexOf('%', start);
            if (percent == -1) {
                if (mode) throw new ScriptError(
                    "Trailing input missing closing '%' after '" + pattern.substring(start) + "'");
                final Element element = new Literal(pattern.substring(start).trim());
                addElement(element, list, start, percent, pattern);
                break;
            }
            final String chunk = pattern.substring(start, percent).trim();
            final Element element;
            if (mode) element = new Input(chunk);
            else element = new Literal(chunk);
            addElement(element, list, start, percent, pattern);
            mode = !mode;
            start = percent + 1;
        } while (true);
        return list.toArray(new Element[0]);
    }

    private static void addElement(Element element, List<Element> list, int start, int end, String pattern) {
        if (element instanceof Literal literal && literal.value.isBlank()) return;
        if (element instanceof Input input) {
            if (input.value.isBlank()) throw new ScriptError("Input missing name: '"
                + pattern.substring(0, start).trim() + "???" + pattern.substring(end).trim() + "'");
            if (!list.isEmpty() && list.get(list.size() - 1) instanceof Input previous)
                throw new ScriptError("Input '" + input.value + "' cannot directly follow input '"
                    + previous.value + "' (needs non-whitespace separator)");
        }
        list.add(element);
    }

    private int line() {
        if (parent == null) return -1;
        return parent.getLine();
    }

    @Override
    public boolean matches() {
        if (elements.length < 1) return false;
        if (elements[0] instanceof Literal literal && !input.startsWith(literal.value)) return false;
        if (elements[elements.length - 1] instanceof Literal literal && !input.endsWith(literal.value)) return false;
        if (elements.length > 2) for (final Element element : elements) {
            if (element instanceof Literal literal && !input.contains(literal.value)) return false;
        }
        this.map = new LinkedHashMap<>();
        final int length = elements.length;
        int start = 0;
        for (int i = 0; i < length; i++) {
            if (start >= input.length()) return false;
            while (Character.isWhitespace(input.charAt(start))) start++;
            final Element element = elements[i], next;
            if (element instanceof Literal literal) {
                if (!input.substring(start).startsWith(literal.value)) return false;
                start += literal.value.length();
                continue;
            }
            if (!(element instanceof Input keyed)) return false;
            if (i < length - 1) {
                next = elements[i + 1];
                int check = start;
                do {
                    check = input.indexOf(next.value(), check);
                    if (check == -1) return false;
                    final String part = input.substring(start, check).trim();
                    if (!part.isEmpty()) {
                        final Statement<?> statement = parent.parse(part);
                        if (statement != null) {
                            this.map.put(keyed.value, statement);
                            break;
                        }
                    }
                    check = check + next.value().length();
                } while (check < input.length());
                start = check;
            } else {
                final String part = input.substring(start).trim();
                final Statement<?> statement = parent.parse(part);
                if (statement == null) return false;
                this.map.put(keyed.value, statement);
            }
        }
        return true;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        if (map.size() != inputs) throw new ScriptError("Line " + this.line()
            + ": Wrong number of post-parse inputs for '" + pattern + "', found "
            + map.size() + " but expected " + inputs);
        return new LocalSyntaxStatement(pattern, function, map);
    }

    @Override
    public void close() throws ScriptError {
        this.map = null;
        super.close();
    }

    public interface Element {

        String value();

    }

    public record Input(String value) implements Element {
    }

    public record Literal(String value) implements Element {
    }

}
