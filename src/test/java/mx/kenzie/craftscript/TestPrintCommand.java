package mx.kenzie.craftscript;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.centurion.CommandResult;
import mx.kenzie.centurion.MinecraftCommand;
import org.bukkit.command.CommandSender;

import static mx.kenzie.centurion.Arguments.GREEDY_STRING;

public class TestPrintCommand extends MinecraftCommand {

    TestPrintCommand() {
        super("Test command.");
    }

    @Override
    public MinecraftBehaviour create() {
        return command("print")
            .arg(GREEDY_STRING, this::say)
            .lapse(this::lapse);
    }

    private CommandResult lapse(CommandSender sender) {
        return CommandResult.NO_BEHAVIOUR;
    }

    private CommandResult say(CommandSender sender, Arguments arguments) {
        sender.sendMessage(arguments.<String>get(0));
        return CommandResult.PASSED;
    }

}
