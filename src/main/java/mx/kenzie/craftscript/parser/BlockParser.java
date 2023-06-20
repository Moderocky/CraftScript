package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockParser extends BasicParser {

    @Override
    public boolean matches() {
        return input.equals("{");
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        final List<Statement<?>> either = new ArrayList<>(), or = new ArrayList<>();
        boolean choice = false;
        List<Statement<?>> list = either;
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
                list.add(statement);
            } catch (IOException ex) {
                throw new ScriptError("Error reading line " + parent.getLine() + " in block.", ex);
            }
        } while (true);
        if (choice) return new ChoiceBlockStatement(either.toArray(new Statement[0]), or.toArray(new Statement[0]));
        return new BlockStatement(either.toArray(new Statement[0]));
    }

}
