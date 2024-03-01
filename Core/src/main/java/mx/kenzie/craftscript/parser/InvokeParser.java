package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.InvokeStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.VariableReferenceStatement;

import java.util.ArrayList;
import java.util.List;

public class InvokeParser extends BasicParser {

    private String name;
    private Statement<?> function;
    private Statement<?>[] arguments;

    @Override
    public boolean matches() {
        final int open = input.indexOf('('), close = input.lastIndexOf(')');
        if (open == -1) return false;
        if (close != input.length() - 1) return false;
        final String source = input.substring(0, open).trim();
        if (source.isEmpty()) return false;
        this.function = parent.parse(source);
        if (function == null) return false;
        if (function instanceof VariableReferenceStatement reference) this.name = reference.name();
        final String header = input.substring(open + 1, close).trim();
        this.arguments = this.getArguments(header);
        return arguments != null;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new InvokeStatement<>(name, function, arguments);
    }

    public Statement<?>[] getArguments(String header) {
        if (header.isEmpty()) return new Statement[0];
        final List<Statement<?>> statements = new ArrayList<>();
        int start = 0, end;
        next:
        do {
            do {
                final int comma = end = header.indexOf(',', start);
                if (comma == -1) end = header.length();
                final String string = header.substring(start, end).trim();
                if (string.isEmpty()) return null;
                ++end;
                final Statement<?> statement = parent.parse(string);
                if (statement == null) continue;
                statements.add(statement);
                start = end;
                continue next;
            } while (end < header.length());
            return null;
        } while (start < header.length());
        return statements.toArray(new Statement[0]);
    }

    @Override
    public void close() throws ScriptError {
        this.name = null;
        this.function = null;
        this.arguments = null;
        super.close();
    }

}
