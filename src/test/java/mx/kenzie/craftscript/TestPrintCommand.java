package mx.kenzie.craftscript;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.centurion.CommandResult;
import mx.kenzie.centurion.MinecraftCommand;
import mx.kenzie.centurion.RelativeVector;
import org.bukkit.command.CommandSender;

import static mx.kenzie.centurion.Arguments.*;

public class TestPrintCommand extends MinecraftCommand {

    TestPrintCommand() {
        super("Test command.");
    }

    @Override
    public MinecraftBehaviour create() {
        return command("print")
            .arg(OFFSET, this::vector)
            .arg(INTEGER, this::integer)
            .arg(DOUBLE, this::number)
            .arg(GREEDY_STRING, this::say)
            .lapse(this::lapse);
    }

    private CommandResult vector(CommandSender sender, Arguments arguments) {
        sender.sendMessage("vector " + arguments.<RelativeVector>get(0));
        return CommandResult.PASSED;
    }

    private CommandResult integer(CommandSender sender, Arguments arguments) {
        sender.sendMessage("integer " + arguments.<Integer>get(0));
        return CommandResult.PASSED;
    }

    private CommandResult number(CommandSender sender, Arguments arguments) {
        sender.sendMessage("double " + arguments.<Double>get(0));
        return CommandResult.PASSED;
    }

    private CommandResult lapse(CommandSender sender) {
        return CommandResult.NO_BEHAVIOUR;
    }

    private CommandResult say(CommandSender sender, Arguments arguments) {
        sender.sendMessage(arguments.<String>get(0));
        return CommandResult.PASSED;
    }

}
