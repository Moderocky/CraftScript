package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Executable;

import java.util.Objects;

public record Script(String name,
                     Statement<?>... statements) implements AbstractScript, Statement<Object>, Executable<Object> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final Script script)) return false;
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

}
