package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.emitter.Event;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.ListenerStatement;
import mx.kenzie.craftscript.statement.LiteralStatement;
import mx.kenzie.craftscript.statement.MapStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.VariableHelper;
import org.bukkit.NamespacedKey;

public class ListenerParser extends BasicParser {

    private Statement<?> key;
    private Statement<?> details;
    private Statement<?> task;

    @Override
    public boolean matches() {
        if (!input.startsWith("on ")) return false;
        if (input.length() < 5) return false;
        final String string = input.substring(3).trim();
        int space = string.indexOf(' ');
        do {
            if (space < 1) return false;
            final String before = string.substring(0, space).trim(), after;
            after = string.substring(space + 1).trim();
            check:
            {
                if (before.isEmpty()) break check;
                if (after.isEmpty()) return false;
                if (before.contains(":") && before.contains("[") && before.contains("]")) {
                    if (!readKeyQuery(before)) break check;
                    if (this.readTask(after)) return true;
                }
                if (before.contains(":")) {
                    if (!readKeySimple(before)) break check;
                    if (this.readTask(after)) return true;
                }
                if (!this.readKeyOther(before)) break check;
                if (this.readTask(after)) return true;
            }
            space = string.indexOf(' ', space + 1);
        } while (space > 0 && space < string.length());
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        return new ListenerStatement(key, details, task);
    }

    private boolean readTaskSimple(String string) {
        final Statement<?> statement = this.parent.parse(string);
        if (statement == null) return false;
        this.task = statement;
        return true;
    }

    private boolean readTask(String string) {
        final VariableHelper helper = VariableHelper.instance(), clone = helper.clone();
        try {
            clone.assign("event", Event.class);
            VariableHelper.local.set(clone);
            if (this.readTaskSimple(string)) return true;
            int space = string.indexOf(' ');
            do {
                if (space < 1) return false;
                final String before = string.substring(0, space).trim(), after;
                after = string.substring(space + 1).trim();
                if (this.readQuery(before) && this.readTaskSimple(after)) return true;
                space = string.indexOf(' ', space + 1);
            } while (space > 0 && space < string.length());
            return false;
        } finally {
            VariableHelper.local.set(helper);
            clone.purge();
        }
    }

    private boolean readKeyOther(String string) {
        final Statement<?> statement = this.parent.parse(string);
        if (statement == null) return false;
        this.key = statement;
        return true;
    }

    private boolean readKeySimple(String string) {
        try {
            final NamespacedKey key = NamespacedKey.fromString(string);
            this.key = new LiteralStatement(key);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private boolean readQuery(String string) {
        if (!string.startsWith("[") || !string.endsWith("]")) return false;
        final Statement<?> statement = this.parent.parse(string);
        if (!(statement instanceof MapStatement)) return false;
        this.details = statement;
        return true;
    }

    private boolean readKeyQuery(String string) {
        final int bracket = string.indexOf('[');
        final String before = string.substring(0, bracket).trim();
        final String after = string.substring(bracket).trim();
        return this.readKeySimple(before) && this.readQuery(after);
    }

    @Override
    public void close() throws ScriptError {
        super.close();
        this.key = null;
        this.details = null;
        this.task = null;
    }

}
