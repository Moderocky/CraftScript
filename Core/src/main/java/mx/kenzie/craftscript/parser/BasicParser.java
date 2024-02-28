package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptSourceParser;

public abstract class BasicParser implements Parser {

    protected String input;
    protected ScriptSourceParser parent;
    private volatile boolean inUse;

    public BasicParser() {
    }

    @Override
    public void insert(String input, ScriptSourceParser parent) {
        this.inUse = true;
        this.input = input;
        this.parent = parent;
    }

    @Override
    public void close() throws ScriptError {
        this.input = null;
        this.parent = null;
        this.inUse = false;
    }

    public boolean canUse() {
        return !inUse;
    }

}
