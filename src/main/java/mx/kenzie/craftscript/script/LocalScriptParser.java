package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Warning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class LocalScriptParser implements ScriptLoader, ScriptParser {


    protected final ScriptLoader parent;
    protected final BufferedReader reader;
    protected final List<Supplier<Parser>> local = new LinkedList<>();
    private int line;
    private int modCount;

    public LocalScriptParser(ScriptLoader parent, BufferedReader reader) {
        this.parent = parent;
        this.reader = reader;
    }


    @Override
    public Script parse(String name, InputStream stream) throws IOException {
        return parent.parse(name, stream);
    }

    @Override
    public AbstractScript parse(InputStream stream) throws IOException {
        return parent.parse(stream);
    }

    @Override
    public Statement<?> parseLine() throws IOException {
        do {
            this.line++;
            final String line = this.readLine();
            if (line == null) throw new ScriptError("Reached end of script when expecting line.");
            if (line.isBlank()) continue;
            if (this.checkEmpty(line)) continue;
            final Statement<?> statement = this.parse(line.trim());
            if (statement == null)
                throw new ScriptError("Line " + this.line + ": '" + line + "' was not recognised.");
            return statement;
        } while (true);
    }

    @Override
    public Statement<?> parse(String line) {
        this.modCount++;
        if (modCount > 12) throw new ScriptError("Line " + this.line + ": the statement '"
            + line + "' will recurse infinitely and cannot be parsed.");
        try {
            final Iterator<Parser> iterator = this.parsers();
            while (iterator.hasNext()) try (final Parser parser = iterator.next()) {
                assert parser.canUse();
                parser.insert(line, this);
                if (parser.matches()) return parser.parse();
                assert !parser.canUse();
            }
        } finally {
            this.modCount--;
        }
        return null;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public void incrementLine() {
        ++this.line;
    }

    @Override
    public Iterator<Parser> parsers() {
        if (parent == this) throw new ScriptError("Parent parse structure recursion.");
        return new Iterator<>() {
            private final Iterator<Parser> theirs = parent.parsers();
            private final Iterator<Supplier<Parser>> ours = local.iterator();

            @Override
            public boolean hasNext() {
                return theirs.hasNext() || ours.hasNext();
            }

            @Override
            public Parser next() {
                if (theirs.hasNext()) return theirs.next();
                else return ours.next().get();
            }
        };
    }

    @Override
    public String readLine() throws IOException {
        return parent.readLine();
    }

    @Override
    public boolean checkEmpty(String line) {
        return parent.checkEmpty(line);
    }

    @Override
    public void warn(String text) {
        this.parent.warn(text);
    }

    @Override
    public Collection<Warning> warnings() {
        return parent.warnings();
    }

    @Override
    public void register(Supplier<Parser> parser) {
        this.local.add(parser);
    }

}
