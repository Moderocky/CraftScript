package mx.kenzie.craftscript;

import mx.kenzie.craftscript.disk.DirectoryScriptStorage;
import mx.kenzie.craftscript.disk.ScriptStorage;
import mx.kenzie.craftscript.environment.ScriptController;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

public class CraftScriptPlugin extends JavaPlugin {

    public static ScriptController scripts;
    public static CraftScriptPlugin plugin;
    public static DirectoryScriptStorage storage;
    public static CraftScriptConfig config;

    public static String upload(String script, ScriptStorage storage) {
        return "todo"; // todo
    }

    public static String download(String script, ScriptStorage storage) {
        return "todo"; // todo
    }

    @Override
    public void onDisable() {
        CraftScriptPlugin.scripts.close();
        CraftScriptPlugin.scripts = null;
        CraftScriptPlugin.plugin = null;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        final File folder = this.makeFolder(), scripts = new File(folder, "scripts/");
        CraftScriptPlugin.plugin = this;
        CraftScriptPlugin.storage = new DirectoryScriptStorage(scripts);
        CraftScriptPlugin.scripts = new ScriptController();
        CraftScriptPlugin.config = this.loadConfigFile(new File(folder, "config.script"));
    }

    private CraftScriptConfig loadConfigFile(File file) {
        final CraftScriptConfig container = new CraftScriptConfig();
        if (!file.exists()) try {
            file.createNewFile();
            try (final OutputStream output = new FileOutputStream(file); final InputStream stream = CraftScriptPlugin.class.getClassLoader().getResourceAsStream("config.script")) {
                stream.transferTo(output);
                output.flush();
            }
            try (final InputStream stream = new FileInputStream(file)) {
                final Script script = ScriptLoader.BASIC.parse("config.script", stream);
                script.execute(new Context(Bukkit.getConsoleSender(), scripts.getScriptManager(), container));
            } catch (IOException | ScriptError ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Unable to load CraftScript config.", ex);
            }
        } catch (IOException | NullPointerException problem) {
            try (final InputStream stream = CraftScriptPlugin.class.getClassLoader().getResourceAsStream("config.script")) {
                final Script script = ScriptLoader.BASIC.parse("config.script", stream);
                script.execute(new Context(Bukkit.getConsoleSender(), scripts.getScriptManager(), container));
            } catch (IOException | ScriptError ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Unable to load CraftScript config.", ex);
            }
        }
        return container;
    }

    private File makeFolder() {
        final File file = this.getDataFolder();
        if (file.exists()) return file;
        else if (file.getParentFile() == null) file.mkdir();
        else file.mkdirs();
        return file;
    }

}
