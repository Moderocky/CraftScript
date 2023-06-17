package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;

import java.util.function.BiFunction;

public abstract class BinaryParser extends BasicParser {

    protected final String operator;
    protected final BiFunction<Statement<?>, Statement<?>, Statement<?>> creator;
    private final int length;
    protected Statement<?> antecedent, consequent;

    protected BinaryParser(String operator, BiFunction<Statement<?>, Statement<?>, Statement<?>> creator) {
        this.operator = operator;
        this.creator = creator;
        this.length = operator.length();
    }

    @Override
    public boolean matches() {
        if (!input.contains(operator)) return false;
        int begin = 0, equals;
        do {
            equals = input.indexOf(operator, begin);
            if (equals < 1) return false;
            final String first = input.substring(begin, equals).trim();
            if (first.isEmpty()) return false;
            final String second = input.substring(equals + length).trim();
            if (second.isEmpty()) return false;
            begin = equals + length;
            this.antecedent = parent.parse(first);
            if (antecedent == null) continue;
            this.consequent = parent.parse(second);
            if (consequent == null) continue;
            return true;
        } while (true);
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return creator.apply(antecedent, consequent);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.antecedent = null;
        this.consequent = null;
    }

}
