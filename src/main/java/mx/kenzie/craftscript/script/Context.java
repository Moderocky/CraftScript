package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.utility.MapFormat;
import mx.kenzie.craftscript.variable.VariableContainer;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashSet;
import java.util.Set;

public record Context(CommandSender source, ScriptManager manager, VariableContainer variables, Data data) {

    private static final ThreadLocal<Context> local = new ThreadLocal<>();

    public Context(Context context) {
        this(context.source, context.manager, new VariableContainer(context.variables), context.data.clone());
    }

    public Context(CommandSender source, ScriptManager manager) {
        this(source, manager, new VariableContainer());
    }

    public Context(CommandSender source, ScriptManager manager, VariableContainer variables) {
        this(source, manager, variables, new Data());
    }

    public static Context getLocalContext() {
        return local.get();
    }

    public static void setLocalContext(Context context) {
        local.set(context);
    }

    public static void removeLocalContext() {
        local.remove();
    }

    public Set<Kind<?>> getKinds() {
        final Set<Kind<?>> kinds = new LinkedHashSet<>();
        kinds.addAll(data.kinds);
        kinds.addAll(manager.getKinds());
        return kinds;
    }

    public String interpolate(String input) {
        return MapFormat.format(input, variables);
    }

    public static class Data {

        public Script script;
        public Set<MinecraftCommand> localCommands = new LinkedHashSet<>();
        public Set<Kind<?>> kinds = new LinkedHashSet<>();

        @Override
        public Data clone() {
            try {
                return (Data) super.clone();
            } catch (Throwable ex) {
                final Data data = new Data();
                data.script = script;
                return data;
            }
        }

    }

}
