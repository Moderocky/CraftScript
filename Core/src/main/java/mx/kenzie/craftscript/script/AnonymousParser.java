package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.Statement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AnonymousParser implements ElementParser {

    private final ScriptSourceParser parent;
    private final Supplier<Parser>[] parsers;
    private final Map<Supplier<Parser>, Parser> cache = new HashMap<>();

    @SafeVarargs
    public AnonymousParser(ScriptSourceParser parent, Supplier<Parser>... parsers) {
        this.parent = parent;
        this.parsers = parsers;
    }

    @SafeVarargs
    public static AnonymousParser of(ScriptSourceParser parent, Supplier<Parser>... parsers) {
        return new AnonymousParser(parent, parsers);
    }

    @Override
    public Statement<?> parse(String element) {
        for (final Supplier<Parser> supplier : parsers) {
            try (final Parser parser = this.getParser(supplier)) {
                assert parser.canUse();
                parser.insert(element, parent);
                if (parser.matches()) return parser.parse();
                assert !parser.canUse();
            }
        }
        return null;
    }

    private Parser getParser(Supplier<Parser> supplier) {
        final Parser current = cache.get(supplier);
        if (current != null && current.canUse()) return current;
        final Parser replace = supplier.get();
        if (current == null) cache.put(supplier, replace);
        return replace;
    }

    public ScriptSourceParser parent() {
        return parent;
    }

}
