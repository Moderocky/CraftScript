package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.CloseStatement;
import mx.kenzie.craftscript.statement.Statement;

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
        final List<Statement<?>> list = new ArrayList<>();
        do {
            try {
                final Statement<?> statement = parent.parseLine();
                if (statement instanceof CloseStatement) break;
                list.add(statement);
            } catch (IOException ex) {
                throw new ScriptError("Error reading line " + parent.getLine() + " in block.", ex);
            }
        } while (true);
        return new BlockStatement(list.toArray(new Statement[0]));
    }

}
