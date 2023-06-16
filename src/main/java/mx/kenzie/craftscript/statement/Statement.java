package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.Executable;

import java.io.PrintStream;

public interface Statement<Result> extends Executable<Result> {

    default void debug(PrintStream stream) {
        stream.print(this);
    }

    void stringify(PrintStream stream);

}
