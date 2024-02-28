package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.utility.BackgroundTaskExecutor;
import mx.kenzie.craftscript.utility.TaskExecutor;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface ScriptManager<Source> extends Closeable {

    AbstractScript parseScript(String content);

    AbstractScript parseScript(InputStream content);

    Script parseScript(String name, String content);

    Script parseScript(String name, InputStream stream);

    Script loadScript(String name, InputStream stream);

    Script loadScript(String name, String content);

    <Type extends AbstractScript> Type loadScript(Type script);

    AbstractScript getScript(String name);

    void deleteScript(String name);

    void deleteScript(AbstractScript script);

    Object runScript(AbstractScript script, Source source);

    Object runScriptSafely(AbstractScript script, Source source);

    AbstractScript[] getScripts();

    void printError(ScriptError error, Source sender);

    Map<String, Object> getGlobalVariables();

    @Override
    void close();

    void setExecutor(TaskExecutor executor);

    void setBackgroundExecutor(BackgroundTaskExecutor executor);

    boolean isTest();

    void registerKind(Kind<?> kind);

    Set<Kind<?>> getKinds();

    boolean runCommand(Context<?> context, String command);

    Object executeOnPrimary(Context<?> context, Supplier<Object> supplier);

    ScriptSourceParser getParser();

    void println(Source source, String line);

}
