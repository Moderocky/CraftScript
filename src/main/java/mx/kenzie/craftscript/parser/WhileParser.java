package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.WhileStatement;

public class WhileParser extends IfParser {

    @Override
    public boolean matches() {
        if (!input.startsWith("while ")) return false;
        return this.parse(6);
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new WhileStatement(check, then);
    }

}
