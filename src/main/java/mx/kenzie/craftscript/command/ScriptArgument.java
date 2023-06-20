package mx.kenzie.craftscript.command;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.centurion.TypedArgument;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.LibraryScript;
import mx.kenzie.craftscript.utility.ScriptController;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ScriptArgument extends TypedArgument<AbstractScript> {

    private final ScriptController controller;

    public ScriptArgument(ScriptController controller) {
        super(AbstractScript.class);
        this.controller = controller;
    }

    @Override
    public boolean matches(String s) {
        return controller.getScript(MinecraftCommand.<CommandSender>getContext().getSender(), s) != null;
    }

    @Override
    public AbstractScript parse(String s) {
        return controller.getScript(MinecraftCommand.<CommandSender>getContext().getSender(), s);
    }

    @Override
    public String[] possibilities() {
        final CommandSender sender = MinecraftCommand.<CommandSender>getContext().getSender();
        final AbstractScript[] scripts = controller.getScripts(sender);
        final List<String> list = new ArrayList<>();
        for (final AbstractScript script : scripts) {
            if (script instanceof LibraryScript) continue;
            list.add(script.name());
        }
        return list.toArray(new String[0]);
    }

}
