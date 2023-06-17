package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.kind.CustomKind;
import mx.kenzie.craftscript.kind.Kind;

import java.util.regex.Pattern;

public class UnknownObject extends VariableContainer {

    public static final Pattern TYPE_NAME = Pattern.compile("#[a-zA-Z_\\-.][a-zA-Z0-9_\\-.]*");
    private static final CustomKind KIND = new CustomKind();

    protected final String name;

    public UnknownObject(String name) {this.name = name;}

    @Override
    public String toString() {
        return name;
    }

    public Kind<UnknownObject> getKind() {
        return KIND;
    }

}
