package mx.kenzie.craftscript.utility;

public interface PrettyPrinter {

    String UNIT = "  ";
    ThreadLocal<String> indent = new ThreadLocal<>();

    static void reset() {
        indent.remove();
    }

    static String getIndent() {
        final String string = indent.get();
        if (string == null) return "";
        return string;
    }

    static int indentLevel() {
        final String string = indent.get();
        if (string == null || string.isEmpty()) return 0;
        return string.length() / UNIT.length();
    }

    static void incrementIndent() {
        indent.set(UNIT + getIndent());
    }

    static void decrementIndent() {
        final String current = getIndent();
        if (current.length() <= UNIT.length()) indent.set("");
        else indent.set(current.substring(UNIT.length()));
    }

}
