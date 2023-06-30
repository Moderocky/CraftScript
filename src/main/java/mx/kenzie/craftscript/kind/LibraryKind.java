package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.LibraryObject;

public class LibraryKind extends Kind<LibraryObject> {

    public LibraryKind() {
        super(LibraryObject.class);
    }

    @Override
    public Object getProperty(LibraryObject thing, String property) {
        return switch (property) {
            case "type" -> this;
            case "properties" -> thing.keySet();
            default -> thing.get(property);
        };
    }

}
