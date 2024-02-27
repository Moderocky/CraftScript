package mx.kenzie.craftscript.disk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public interface ScriptStorage {

    static boolean isValidScriptName(String name) {
        if (!name.endsWith(".script")) return false;
        if (name.contains("\\") || name.contains(">") || name.contains("<") || name.contains("\"")
            || name.contains("|") || name.contains("?") || name.contains(".."))
            return false;
        return !name.contains(" ");
    }

    Collection<String> knownScripts();

    boolean scriptExists(String name);

    InputStream openScript(String name) throws ScriptNotFoundException, IOException;

    /**
     * Creates a new empty script (e.g. its script file, if this is a filesystem manager).
     *
     * @param name The valid script name (including the extension '.script')
     * @return If a new script was created -- false could indicate a failure, an error, or that the script exists already
     */
    boolean createScript(String name);

    boolean deleteScript(String name);

    default OutputStream scriptWriter(String name) throws ScriptNotFoundException, IOException {
        return this.scriptWriter(name, false);
    }

    OutputStream scriptWriter(String name, boolean append) throws IOException;

}
