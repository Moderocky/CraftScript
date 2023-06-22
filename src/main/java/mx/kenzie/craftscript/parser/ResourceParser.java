package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.LiteralStatement;
import mx.kenzie.craftscript.statement.Statement;
import org.bukkit.NamespacedKey;

public class ResourceParser extends BasicParser {

    private String[] parts;

    private static boolean isValidNamespaceChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-';
    }

    private static boolean isValidKeyChar(char c) {
        return isValidNamespaceChar(c) || c == '/';
    }

    private static boolean isValidNamespace(String namespace) {
        if (namespace == null || namespace.isBlank()) return false;
        for (final char c : namespace.toCharArray()) if (!isValidNamespaceChar(c)) return false;
        return true;
    }

    private static boolean isValidKey(String key) {
        if (key == null || key.isBlank()) return false;
        for (final char c : key.toCharArray()) if (!isValidKeyChar(c)) return false;
        return true;
    }

    @Override
    public boolean matches() {
        if (!input.contains(":")) return false;
        this.parts = input.split(":", 2);
        return parts.length == 2 && isValidNamespace(parts[0]) && isValidKey(parts[1]);
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        final NamespacedKey key = new NamespacedKey(parts[0], parts[1]);
        return new LiteralStatement(key);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.parts = null;
    }

}
