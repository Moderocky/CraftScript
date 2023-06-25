package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.CommentParser;
import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.CloseStatement;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.statement.Statement;

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
    private int line;
    private BufferedReader reader;

    @SafeVarargs
    public SimpleScriptLoader(Supplier<Parser>... parsers) {
        this.parsers = parsers;
    }

    @SafeVarargs
    public final void addParsers(Supplier<Parser>... parsers) {
        final List<Supplier<Parser>> list = new ArrayList<>(this.parsers.length + parsers.length);
        list.addAll(List.of(this.parsers));
        list.addAll(List.of(parsers));
        this.parsers = list.toArray(new Supplier[0]);
    }

    @Override
    public AbstractScript parse(InputStream stream) throws IOException {
        return new AnonymousScript(this.parseStatements(stream));
    }

    @Override
    public synchronized Script parse(String name, InputStream stream) throws IOException {
        return new Script(name, this.parseStatements(stream));
    }

    private Statement<?>[] parseStatements(InputStream stream) throws IOException {
        this.line = 0;
        final List<Statement<?>> list = new ArrayList<>();
        try {
            this.reader = new BufferedReader(new InputStreamReader(stream));
            do {
                this.line++;
                final String line = reader.readLine();
                if (line == null) break;
                if (line.isBlank()) continue;
                if (this.checkEmpty(line)) continue;
                final Statement<?> statement = this.parse(line.trim());
                if (statement == null)
                    throw new ScriptError("Line " + this.line + ": '" + line + "' was not recognised.");
                if (statement instanceof CloseStatement)
                    throw new ScriptError("Line " + this.line + ": '" + line + "' closed an un-opened block.");
                list.add(new LineStatement(statement, this.line));
            } while (true);
        } finally {
            if (reader != null) reader.close();
        }
        return list.toArray(new Statement[0]);
    }

    @Override
    public Statement<?> parseLine() throws IOException {
        do {
            this.line++;
            final String line = reader.readLine();
            if (line == null) throw new ScriptError("Reached end of script when expecting line.");
            if (line.isBlank()) continue;
            if (this.checkEmpty(line)) continue;
            final Statement<?> statement = this.parse(line.trim());
            if (statement == null)
                throw new ScriptError("Line " + this.line + ": '" + line + "' was not recognised.");
            return statement;
        } while (true);
    }

    public boolean checkEmpty(String line) {
        try (final Parser parser = comment) {
            parser.insert(line.trim(), this);
            return (parser.matches() && parser.parse() == null);
        }
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

    private Parser getParser(Supplier<Parser> supplier) {
        final Parser current = cache.get(supplier);
        if (current != null && current.canUse()) return current;
        final Parser replace = supplier.get();
        if (current == null) cache.put(supplier, replace);
        return replace;
    }

    public Iterator<Parser> parsers() {
        return new Iterator<>() {
            int index;

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
    public int getLine() {
        return line;
    }

}
