package mx.kenzie.craftscript.disk;

import java.io.FileNotFoundException;

public class ScriptNotFoundException extends FileNotFoundException {

    public ScriptNotFoundException() {
        super();
    }

    public ScriptNotFoundException(String s) {
        super(s);
    }

}
