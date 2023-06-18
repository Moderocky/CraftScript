package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;

public record AnonymousScript(
    Statement<?>... statements) implements AbstractScript, Statement<Object>, Executable<Object> {

    @Override
    public String toString() {
        return this.name();
    }

    @Override
    public String name() {
        return "<anonymous>.script";
    }

}
