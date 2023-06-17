package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.Statement;

import java.io.Closeable;

/**
 * Parsers test and create statements from an input line.
 * 1. The input is inserted.
 * 2. `matches` is checked.
 * 3. if `matches` passed, `parse` is called.
 * 4. `close` is called.
 */
public interface Parser extends Closeable {

    void insert(String input, ScriptParser parent);

    boolean matches();

    Statement<?> parse() throws ScriptError;

    @Override
    void close() throws ScriptError;

    default boolean canUse() {
        return true;
    }

}
