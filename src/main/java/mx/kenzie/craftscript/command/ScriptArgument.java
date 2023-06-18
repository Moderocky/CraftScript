package mx.kenzie.craftscript.command;

import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.centurion.TypedArgument;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.utility.ScriptController;
import org.bukkit.command.CommandSender;

public class ScriptArgument extends TypedArgument<Script> {

    private final ScriptController controller;

    public ScriptArgument(ScriptController controller) {
        super(Script.class);
        this.controller = controller;
    }

    @Override
    public boolean matches(String s) {
        return controller.getScript(MinecraftCommand.<CommandSender>getContext().getSender(), s) != null;
    }

    @Override
    public Script parse(String s) {
        return controller.getScript(MinecraftCommand.<CommandSender>getContext().getSender(), s);
    }

    @Override
    public String[] possibilities() {
        final Script[] scripts = controller.getScripts(MinecraftCommand.<CommandSender>getContext().getSender());
        final String[] strings = new String[scripts.length];
        for (int i = 0; i < scripts.length; i++) strings[i] = scripts[i].name();
        return strings;
    }

}
