package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.LocalFunctionParser;
import mx.kenzie.craftscript.parser.Parser;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableStatement;
import mx.kenzie.craftscript.utility.Warning;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

public class DoBlockParser implements ScriptParser {

    protected final ScriptParser parent;
    protected final Statement<?> source;
    private boolean dirty;

    public DoBlockParser(Statement<?> source, ScriptParser parent) {
        this.parent = parent;
        this.source = source;
    }

    public Statement<?> getSource() {
        return source;
    }

    @Override
    public Statement<?> parseLine() throws IOException {
        do {
            this.dirty = false;
            this.parent.incrementLine();
            final String line = this.readLine();
            if (line == null) throw new ScriptError("Reached end of script when expecting line.");
            if (line.isBlank()) continue;
            if (this.checkEmpty(line)) continue;
            final Statement<?> statement = this.parse(line.trim()), result;
            if (statement instanceof VariableStatement) result = this.localFunction(line.trim());
            else result = statement;
            if (result == null)
                throw new ScriptError("Line " + this.getLine() + ": '" + line + "' was not recognised.");
            return result;
        } while (true);
    }

    protected Statement<?> localFunction(String line) {
        try (Parser parser = new LocalFunctionParser()) {
            parser.insert(line, this);
            if (parser.matches()) return parser.parse();
            return null;
        }
    }

    @Override
    public Statement<?> parse(String line) {
        final Iterator<Parser> parsers = this.parsers();
        while (parsers.hasNext()) try (Parser parser = parsers.next()) {
            assert parser.canUse();
            parser.insert(line, this);
            if (parser.matches()) return parser.parse();
            assert !parser.canUse();
        }
        if (dirty) return null;
        try (Parser parser = new LocalFunctionParser()) {
            parser.insert(line, this);
            if (parser.matches()) return parser.parse();
            return null;
        }
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public void incrementLine() {
        this.parent.incrementLine();
    }

    @Override
    public Iterator<Parser> parsers() {
        return parent.parsers();
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
        this.parent.register(parser);
    }

    public void flagDirty() {
        this.dirty = true;
    }

}
