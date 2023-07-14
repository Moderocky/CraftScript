package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.utility.MapFormat;
import mx.kenzie.craftscript.variable.VariableContainer;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public record Context(CommandSender source, ScriptManager manager, VariableContainer variables, Data data) {

    private static final ThreadLocal<Context> local = new ThreadLocal<>();

    public Context(Context context) {
        this(context.source, context.manager, new VariableContainer(context.variables), context.data.clone());
        this.data.parentContext = context;
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

    public static Context requireLocalContext() {
        final Context context = getLocalContext();
        if (context == null) throw new ScriptError("No script environment context is available.");
        return context;
    }

    public static void removeLocalContext() {
        local.remove();
    }

    @SuppressWarnings("unchecked")
    public static <Type> Type executeOnPrimary(Supplier<Type> supplier) {
        final Context context = requireLocalContext();
        return (Type) context.manager.executeOnPrimary(context, (Supplier<Object>) supplier);
    }

    public Set<Kind<?>> getKinds() {
        final Set<Kind<?>> kinds = new LinkedHashSet<>();
        kinds.addAll(data.kinds);
        kinds.addAll(manager.getKinds());
        return kinds;
    }

    public Iterator<Kind<?>> kinds() {
        return new Iterator<>() {
            final Iterator<Kind<?>> ours = data.kinds.iterator(), theirs = manager.getKinds().iterator();

            @Override
            public boolean hasNext() {
                return ours.hasNext() || theirs.hasNext();
            }

            @Override
            public Kind<?> next() {
                return ours.hasNext() ? ours.next() : theirs.next();
            }
        };
    }

    public Context getParentContext() {
        return data.parentContext;
    }

    public Context getRootContext() {
        var context = this;
        do {
            if (context.data == null) return context;
            if (context.data.parentContext == null) return context;
            context = data().parentContext;
        } while (context != null);
        return this;
    }

    public String interpolate(String input) {
        return MapFormat.format(input, variables);
    }

    public int getLine() {
        if (data == null) return -1;
        if (data.line == null) return -1;
        return data.line.line();
    }

    public static class Data {

        public AbstractScript script;
        public Set<MinecraftCommand> localCommands = new LinkedHashSet<>();
        public Set<Kind<?>> kinds = new LinkedHashSet<>();
        public LineStatement line;
        public Context parentContext;

        @Override
        public Data clone() {
            final Data data = new Data();
            data.script = script;
            data.localCommands = new LinkedHashSet<>(localCommands);
            data.kinds = new LinkedHashSet<>(kinds);
            data.line = line;
            return data;
        }

    }

}
