package mx.kenzie.craftscript.script;

import mx.kenzie.centurion.Command;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.statement.LineStatement;
import mx.kenzie.craftscript.variable.VariableContainer;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public record Context<Source>(Source source, ScriptManager<Source> manager, VariableContainer variables, Data data) {

    private static final ThreadLocal<Context<?>> local = new ThreadLocal<>();
    private static final Context<PrintStream> system = new Context<>(System.out, new SystemScriptManager(SimpleScriptLoader.BASIC));

    public Context(Context<Source> context) {
        this(context.source, context.manager, new VariableContainer(context.variables), context.data.clone());
        this.data.parentContext = context;
    }

    public Context(Source source, ScriptManager<Source> manager) {
        this(source, manager, new VariableContainer());
    }

    public Context(Source source, ScriptManager<Source> manager, VariableContainer variables) {
        this(source, manager, variables, new Data());
    }

    public static Context<PrintStream> system() {
        return system;
    }

    public static Context<?> getLocalContext() {
        return local.get();
    }

    public static void setLocalContext(Context<?> context) {
        local.set(context);
    }

    public static Context<?> requireLocalContext() {
        final Context<?> context = getLocalContext();
        if (context == null) throw new ScriptError("No script environment context is available.");
        return context;
    }

    public static void removeLocalContext() {
        local.remove();
    }

    @SuppressWarnings("unchecked")
    public static <Type> Type executeOnPrimary(Supplier<Type> supplier) {
        final Context<?> context = requireLocalContext();
        return (Type) context.manager.executeOnPrimary(context, (Supplier<Object>) supplier);
    }

    @SuppressWarnings("unchecked")
    public <Type> Type getSource() {
        return (Type) source;
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

    public Context<?> getParentContext() {
        return data.parentContext;
    }

    public Context<?> getRootContext() {
        Context<?> context = this;
        do {
            if (context.data == null) return context;
            if (context.data.parentContext == null) return context;
            context = data().parentContext;
        } while (context != null);
        return this;
    }

    public int getLine() {
        if (data == null) return -1;
        if (data.line == null) return -1;
        return data.line.line();
    }

    public void step() {
        ScriptThread.step();
    }

    public static class Data {

        public AbstractScript script;
        public Set<Command<?>> localCommands = new LinkedHashSet<>();
        public Set<Kind<?>> kinds = new LinkedHashSet<>();
        public LineStatement line;
        public Context<?> parentContext;

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
