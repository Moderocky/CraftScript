package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;
import mx.kenzie.craftscript.statement.EqualsStatement;
import mx.kenzie.craftscript.statement.Statement;

public class EqualsParser extends BasicParser {

    private Statement<?> antecedent, consequent;

    @Override
    public boolean matches() {
        if (!input.contains("==")) return false;
        int begin = 0, equals;
        do {
            equals = input.indexOf("==", begin);
            if (equals < 1) return false;
            final String first = input.substring(begin, equals).trim();
            if (first.isEmpty()) return false;
            final String second = input.substring(equals + 2).trim();
            if (second.isEmpty()) return false;
            begin = equals + 2;
            this.antecedent = parent.parse(first);
            if (antecedent == null) continue;
            this.consequent = parent.parse(second);
            if (consequent == null) continue;
            return true;
        } while (true);
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new EqualsStatement(antecedent, consequent);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.antecedent = null;
        this.consequent = null;
    }

}
