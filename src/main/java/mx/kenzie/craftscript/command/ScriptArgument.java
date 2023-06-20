package mx.kenzie.craftscript.command;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.centurion.TypedArgument;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.utility.ScriptController;
import org.bukkit.command.CommandSender;

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
        final AbstractScript[] scripts = controller.getScripts(MinecraftCommand.<CommandSender>getContext().getSender());
        final String[] strings = new String[scripts.length];
        for (int i = 0; i < scripts.length; i++) strings[i] = scripts[i].name();
        return strings;
    }

}
