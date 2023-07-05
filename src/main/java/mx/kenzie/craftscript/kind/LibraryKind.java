package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.variable.LibraryObject;

import java.util.Set;

public class LibraryKind extends Kind<LibraryObject> {

    public static final LibraryKind LIBRARY = new LibraryKind();

    public LibraryKind() {
        this(null);
    }

    private final LibraryObject library;

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
        if (library != null) {
            final Set<String> set = library.keySet();
            set.add("type");
            set.add("properties");
            return set.toArray(new String[0]);
        } else return new String[]{"type", "properties"};
    }

    @Override
    public String toString() {
        return "#library";
    }

}
