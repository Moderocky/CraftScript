package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.LibraryObject;

public class LibraryKind extends Kind<LibraryObject> {

    public static final LibraryKind LIBRARY = new LibraryKind();

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

    @Override
    public String[] getProperties() {
        return new String[]{"type", "properties"};
    }

}
