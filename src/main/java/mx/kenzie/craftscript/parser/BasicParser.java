package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.ScriptError;

public abstract class BasicParser implements Parser {

    protected String input;
    protected ScriptParser parent;

    public BasicParser() {}

    @Override
    public void insert(String input, ScriptParser parent) {
        this.input = input;
        this.parent = parent;
    }

    @Override
    public void close() throws ScriptError {
        this.input = null;
        this.parent = null;
    }

}
