package mx.kenzie.craftscript.disk;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DirectoryScriptStorage implements ScriptStorage {

    protected final File folder;

    public DirectoryScriptStorage(File folder) {
        this.folder = folder;
    }

    @Override
    public Collection<String> knownScripts() {
        try {
            final File[] files = folder.listFiles();
            if (files == null) return Collections.emptyList();
            final Set<String> set = new HashSet<>();
            for (final File file : files) {
                final String name = file.getName();
                if (ScriptStorage.isValidScriptName(name)) set.add(name);
            }
            return set;
        } catch (Exception ignored) {
        }
        return Collections.emptyList();
    }

    @Override
    public boolean scriptExists(String name) {
        if (!ScriptStorage.isValidScriptName(name)) return false;
        final File file = new File(folder, name);
        return file.exists() && !file.isDirectory();
    }

    @Override
    public InputStream openScript(String name) throws IOException {
        if (!ScriptStorage.isValidScriptName(name)) throw new ScriptNotFoundException(name);
        final File file = new File(folder, name);
        if (!file.exists()) throw new ScriptNotFoundException(name);
        return new FileInputStream(file);
    }

    @Override
    public boolean createScript(String name) {
        if (!ScriptStorage.isValidScriptName(name)) return false;
        final File file = new File(folder, name);
        try {
            return file.createNewFile();
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public boolean deleteScript(String name) {
        if (!ScriptStorage.isValidScriptName(name)) return false;
        final File file = new File(folder, name);
        return file.delete();
    }

    @Override
    public OutputStream scriptWriter(String name, boolean append) throws IOException {
        if (!ScriptStorage.isValidScriptName(name)) throw new ScriptNotFoundException(name);
        final File file = new File(folder, name);
        if (!file.exists()) throw new ScriptNotFoundException(name);
        return new FileOutputStream(file, append);
    }

}
