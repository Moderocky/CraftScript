package mx.kenzie.craftscript;

import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.CloseStatement;
import mx.kenzie.craftscript.statement.Statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SimpleScriptLoader implements ScriptLoader {

    protected final Supplier<Parser>[] parsers;
    private int line;
    private BufferedReader reader;

    public SimpleScriptLoader(Supplier<Parser>... parsers) {this.parsers = parsers;}

    @Override
    public synchronized Script load(String name, InputStream stream) throws IOException {
        this.line = 0;
        final List<Statement<?>> list = new ArrayList<>();
        try {
            this.reader = new BufferedReader(new InputStreamReader(stream));
            do {
                this.line++;
                final String line = reader.readLine();
                if (line == null) break;
                if (line.isBlank()) continue;
                final Statement<?> statement = this.parse(line.trim());
                if (statement == null)
                    throw new ScriptError("Line " + this.line + ": '" + line + "' was not recognised.");
                if (statement instanceof CloseStatement)
                    throw new ScriptError("Line " + this.line + ": '" + line + "' closed an un-opened block.");
                list.add(statement);
            } while (true);
        } finally {
            if (reader != null) reader.close();
        }
        return new Script(name, list.toArray(new Statement[0]));
    }

    @Override
    public Statement<?> parseLine() throws IOException {
        do {
            this.line++;
            final String line = reader.readLine();
            if (line == null) throw new ScriptError("Reached end of script when expecting line.");
            if (line.isBlank()) continue;
            final Statement<?> statement = this.parse(line.trim());
            if (statement == null)
                throw new ScriptError("Line " + this.line + ": '" + line + "' was not recognised.");
            return statement;
        } while (true);
    }

    @Override
    public Statement<?> parse(String line) {
        for (final Supplier<Parser> supplier : parsers) {
            try (Parser parser = supplier.get()) {
                parser.insert(line, this);
                if (parser.matches()) return parser.parse();
            }
        }
        return null;
    }

    @Override
    public int getLine() {
        return line;
    }

}
