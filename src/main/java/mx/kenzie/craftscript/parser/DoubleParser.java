package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.LiteralStatement;
import mx.kenzie.craftscript.statement.Statement;

import java.util.regex.Matcher;

public class DoubleParser extends BasicParser {

    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(
        "^-?\\d+(?:\\.\\d+)?[Dd]?(?![\\d.#FfLl])");
    private static final int LOW = 48, HIGH = 57;
    private Double value;

    @Override
    public boolean matches() {
        if (input.isEmpty()) return false;
        final char c = input.charAt(0);
        if (c != '-' && (c < LOW || c > HIGH)) return false;
        final Matcher matcher = PATTERN.matcher(input);
        if (matcher.matches()) try {
            this.value = Double.valueOf(input);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
        else return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new LiteralStatement(value != null ? value : 0);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.value = null;
    }

}
