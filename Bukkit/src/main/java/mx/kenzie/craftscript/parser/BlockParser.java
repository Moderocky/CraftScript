package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockParser extends BasicParser {

    private boolean single;

    @Override
    public boolean matches() {
        if (input.equals("{")) return true;
        if (!(input.startsWith("{") && input.endsWith("}"))) return false;
        return single = input.substring(1, input.length() - 1).isBlank();
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        final List<Statement<?>> either = new ArrayList<>(), or = new ArrayList<>();
        boolean choice = false;
        List<Statement<?>> list = either;
        if (single) return new BlockStatement();
        do {
            try {
                final Statement<?> statement = parent.parseLine();
                if (statement instanceof CloseStatement) break;
                if (statement instanceof ElseStatement) {
                    if (choice)
                        throw new ScriptError("Line " + parent.getLine() + ": Multiple 'else' blocks were chained.");
                    choice = true;
                    list = or;
                    continue;
                }
                list.add(new LineStatement(statement, parent.getLine()));
            } catch (IOException ex) {
                throw new ScriptError("Error reading line " + parent.getLine() + " in block.", ex);
            }
        } while (true);
        if (choice) return new ChoiceBlockStatement(new BlockStatement(either.toArray(new Statement[0])),
            new BlockStatement(or.toArray(new Statement[0])));
        return new BlockStatement(either.toArray(new Statement[0]));
    }

    @Override
    public void close() throws ScriptError {
        this.single = false;
        super.close();
    }

}
