package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.CommentParser;
import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.CloseStatement;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.VariableHelper;
import mx.kenzie.craftscript.utility.Warning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Supplier;

public class SimpleScriptLoader implements ScriptLoader {

    private final Map<Supplier<Parser>, Parser> cache = new HashMap<>();
    private final CommentParser comment = new CommentParser();
    protected Supplier<Parser>[] parsers;
    private BufferedReader reader;
    private List<Warning> warnings;

    @SafeVarargs
    public SimpleScriptLoader(Supplier<Parser>... parsers) {
        this.parsers = parsers;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final void addParsers(Supplier<Parser>... parsers) {
        final List<Supplier<Parser>> list = new ArrayList<>(this.parsers.length + parsers.length);
        list.addAll(List.of(this.parsers));
        list.addAll(List.of(parsers));
        this.parsers = list.toArray(new Supplier[0]);
    }

    @Override
    public synchronized Script parse(String name, InputStream stream) throws IOException {
        return new Script(name, this.parseStatements(stream));
    }

    @Override
    public AbstractScript parse(InputStream stream) throws IOException {
        return new AnonymousScript(this.parseStatements(stream));
    }

    private Statement<?>[] parseStatements(InputStream stream) throws IOException {
        final LocalScriptParser parser = new LocalScriptParser(this, reader);
        this.warnings = new LinkedList<>();
        final List<Statement<?>> list = new ArrayList<>();
        this.reader = new BufferedReader(new InputStreamReader(stream));
        try (final BufferedReader ignored = this.reader) {
            VariableHelper.init();
            do {
                parser.incrementLine();
                final String line = this.readLine();
                if (line == null) break;
                if (line.isBlank()) continue;
                if (parser.checkEmpty(line)) continue;
                final Statement<?> statement = parser.parse(line.trim());
                if (statement == null)
                    throw new ScriptError("Line " + parser.getLine() + ": '" + line + "' was not recognised.");
                if (statement instanceof CloseStatement)
                    throw new ScriptError("Line " + parser.getLine() + ": '" + line + "' closed an un-opened block.");
                list.add(new LineStatement(statement, parser.getLine()));
            } while (true);
        } finally {
            VariableHelper.tearDown();
            this.reader = null;
        }
        return list.toArray(new Statement[0]);
    }

    @Override
    public Statement<?> parseLine() throws IOException {
        final String line = this.readLine();
        if (line == null) throw new ScriptError("No lines left in reader.");
        if (line.isBlank()) return null;
        if (this.checkEmpty(line)) return null;
        final Statement<?> statement = this.parse(line.trim());
        if (statement == null)
            throw new ScriptError("Line '" + line + "' was not recognised.");
        return statement;
    }

    @Override
    public Statement<?> parse(String line) {
        for (final Supplier<Parser> supplier : parsers) {
            try (final Parser parser = this.getParser(supplier)) {
                assert parser.canUse();
                parser.insert(line, this);
                if (parser.matches()) return parser.parse();
                assert !parser.canUse();
            }
        }
        return null;
    }

    @Override
    public int getLine() {
        return -1;
    }

    @Override
    public void incrementLine() {
        throw new ScriptError("The root loader does not keep line references.");
    }

    @Override
    public Iterator<Parser> parsers() {
        return new Iterator<>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < parsers.length;
            }

            @Override
            public Parser next() {
                final Supplier<Parser> supplier = parsers[index++];
                return SimpleScriptLoader.this.getParser(supplier);
            }
        };
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public boolean checkEmpty(String line) {
        try (final Parser parser = comment) {
            parser.insert(line.trim(), this);
            return (parser.matches() && parser.parse() == null);
        }
    }

    @Override
    public void warn(String text) {
        if (warnings == null) return;
        this.warnings.add(new Warning(this.getLine(), text));
    }

    @Override
    public Collection<Warning> warnings() {
        return warnings;
    }

    @Override
    public void register(Supplier<Parser> parser) {
        throw new ScriptError("The root loader cannot have a local parser registered to it.");
    }

    private Parser getParser(Supplier<Parser> supplier) {
        final Parser current = cache.get(supplier);
        if (current != null && current.canUse()) return current;
        final Parser replace = supplier.get();
        if (current == null) cache.put(supplier, replace);
        return replace;
    }

}
