package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptManager;
import mx.kenzie.craftscript.variable.VariableContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import java.io.PrintStream;

public record ImportStatement(Import... imports) implements Statement<Boolean> {

    public static Import script(String name) {
        return new ImportScript(name);
    }

    public static Boolean execute(Context<?> context, String... names) {
        final VariableContainer container = context.variables();
        final ScriptManager<?> manager = context.manager();
        for (final String name : names) {
            final AbstractScript script = manager.getScript(name + ".script");
            if (script == null) throw new ScriptError("The script '" + name + ".script' is not loaded.");
            final Object result = RunStatement.run(script, context, new VariableContainer());
            container.put(name, result);
        }
        return true;
    }

    public static Import of(Statement<?> statement) {
        if (statement instanceof Import done) return done;
        if (statement instanceof VariableReferenceStatement found) return script(found.name());
        throw new ScriptError("Don't know how to import a '" + statement.stringify() + "'");
    }

    public static Statement<?> variable(Statement<?> source, String target) {
        if (source instanceof VariableReferenceStatement found) return new ImportVariable(target, found.name());
        throw new ScriptError("Don't know how to import from '" + source.stringify() + "'");
    }

    @Override
    public Boolean execute(Context<?> context) throws ScriptError {
        boolean result = true;
        for (Import anImport : imports) {
            result &= (boolean) anImport.execute(context);
        }
        return result;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        for (Import anImport : imports) {
            anImport.debug(stream);
        }
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print("import [");
        boolean first = true;
        for (Import anImport : imports) {
            if (first) first = false;
            else stream.print(", ");
            anImport.stringify(stream);
        }
        stream.print(']');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            Component.text("import ", profile.dark()),
            Component.text('[', profile.pop()),
            Component.join(JoinConfiguration.separator(Component.text(", ", profile.pop())), imports),
            Component.text(']', profile.pop())
        ).hoverEvent(
            Component.textOfChildren(Component.text("Imports resources from an external program.", profile.light()),
                Component.newline(), this.printReturnType(profile)));
    }

    @Override
    public Class<? extends Boolean> returnType() {
        return Boolean.class;
    }

    public interface Import extends Statement<Object>, VariableReferenceStatement {

        @Override
        default Class<? extends Boolean> returnType() {
            return Boolean.class;
        }

    }

}

record ImportScript(String name) implements ImportStatement.Import {

    public Boolean execute(Context<?> context) {
        final VariableContainer container = context.variables();
        final ScriptManager<?> manager = context.manager();
        final AbstractScript script = manager.getScript(name + ".script");
        if (script == null) throw new ScriptError("The script '" + name + ".script' is not loaded.");
        final Object result = RunStatement.run(script, context, new VariableContainer());
        container.put(name, result);
        return true;
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(name);
    }

}

record ImportVariable(String name, String script) implements ImportStatement.Import {

    public Boolean execute(Context<?> context) {
        final VariableContainer container = context.variables(), theirs = new VariableContainer();
        final ScriptManager<?> manager = context.manager();
        final AbstractScript script = manager.getScript(script() + ".script");
        if (script == null) throw new ScriptError("The script '" + script + ".script' is not loaded.");
        RunStatement.run(script, context, theirs);
        if (name.equals("*")) container.putAll(theirs);
        else container.put(name, theirs.get(name));
        return true;
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print(name);
        stream.print(" from ");
        stream.print(script);
    }

}
