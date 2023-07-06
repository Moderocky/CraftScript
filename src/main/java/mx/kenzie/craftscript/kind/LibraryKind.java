package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.LibraryObject;

import static mx.kenzie.craftscript.kind.CustomKind.getSpecialProperties;

public class LibraryKind extends Kind<LibraryObject> {

    public static final LibraryKind LIBRARY = new LibraryKind();
    private final LibraryObject library;

    public LibraryKind() {
        this(null);
    }

    public LibraryKind(LibraryObject library) {
        super(LibraryObject.class);
        this.library = library;
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
        return getSpecialProperties(library);
    }

    @Override
    public String toString() {
        return "#library";
    }

}
