package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.SuperFunctionStatement;
import mx.kenzie.craftscript.statement.VariableAssignmentStatement;
import mx.kenzie.craftscript.statement.VariableStatement;
import mx.kenzie.craftscript.utility.VariableHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuperFunctionParser extends BasicParser {

    private Statement<?> block;
    private List<Statement<?>> header;

    @Override
    public boolean matches() {
        if (!input.startsWith("fn")) return false;
        if (input.length() < 4) return false;
        final int open = input.indexOf('(');
        if (open < 2) return false;
        if (!input.substring(0, open).trim().equals("fn")) return false;
        final VariableHelper helper = VariableHelper.instance(), child = helper.clone();
        try {
            child.purge(); // outer variables aren't available in a function
            VariableHelper.local.set(child);
            int close, start = open;
            do {
                close = input.indexOf(')', start);
                if (close == -1) return false;
                start = close + 1;
            } while (!this.header(input.substring(open + 1, close).trim()));
            final String string = input.substring(close + 1).trim();
            if (string.isEmpty()) return false;
            this.block = parent.parse(string);
        } finally {
            VariableHelper.local.set(helper);
        }
        return block != null && header != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new SuperFunctionStatement(header, block);
    }

    private boolean header(String string) {
        if (string.isEmpty()) {
            this.header = Collections.emptyList();
            return true;
        }
        final List<Statement<?>> parameters = new ArrayList<>(6);
        if (!this.parameter(string, 0, parameters)) return false;
        Collections.reverse(parameters);
        this.header = parameters;
        return true;
    }

    private boolean parameter(String string, int start, List<Statement<?>> parameters) {
        do {
            final int comma = string.indexOf(',', start);
            final String section;
            if (comma == -1) section = string;
            else section = string.substring(start, comma).trim();
            start = comma + 1;
            final Statement<?> statement = parent.parse(section);
            if (statement == null && comma == -1) break;
            else if (this.isOkayInHeader(statement) && (comma == -1 || this.parameter(string, start, parameters))) {
                parameters.add(statement);
                return true;
            }
        } while (true);
        return false;
    }

    private boolean isOkayInHeader(Statement<?> statement) {
        return statement instanceof VariableStatement || statement instanceof VariableAssignmentStatement;
    }

    @Override
    public void close() throws ScriptError {
        this.block = null;
        this.header = null;
        super.close();
    }

}
