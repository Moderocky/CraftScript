package mx.kenzie.craftscript.command;

import mx.kenzie.centurion.Command;
import mx.kenzie.centurion.TypedArgument;
import mx.kenzie.craftscript.CraftScriptPlugin;
import mx.kenzie.craftscript.disk.ScriptStorage;

import java.util.ArrayList;
import java.util.List;

public class ScriptNameArgument extends TypedArgument<String> {

    public ScriptNameArgument() {
        super(String.class);
        this.label = "script name";
        this.description = "The name of the script.";
    }

    @Override
    public boolean matches(String s) {
        return ScriptStorage.isValidScriptName(s);
    }

    @Override
    public String parse(String s) {
        return s;
    }

    @Override
    public String[] possibilities() {
        final List<String> list = new ArrayList<>();
        check:
        if (Command.getContext() != null) {
            list.addAll(CraftScriptPlugin.scripts.getRuntime().getStorage().knownScripts());
            final String input = Command.getContext().getRawInput();
            if (input.endsWith(" ")) break check;
            final String[] parts = input.split(" ");
            final String last = parts[parts.length - 1];
            if (last.contains(".")) list.add(last.substring(0, last.lastIndexOf('.')) + ".script");
            else list.add(last + ".script");
        }
        return list.toArray(new String[0]);
    }

}
