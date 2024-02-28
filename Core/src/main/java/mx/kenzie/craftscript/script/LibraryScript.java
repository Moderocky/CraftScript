package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.variable.LibraryObject;

import java.util.Objects;

public record LibraryScript(String name, Statement<?>... statements)
    implements AbstractScript, Statement<Object> {

    public LibraryScript(String name, Statement<?>... statements) {
        if (name.endsWith(".script")) this.name = name;
        else this.name = name + ".script";
        this.statements = statements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final LibraryScript script)) return false;
        return Objects.equals(name, script.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Class<? extends LibraryObject> returnType() {
        return LibraryObject.class;
    }

}
