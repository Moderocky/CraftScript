package mx.kenzie.craftscript.kind;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.core.CheckedFunction;
import mx.kenzie.craftscript.script.core.InternalStatement;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static mx.kenzie.craftscript.kind.Kinds.STRING;

public class CommandSenderKind<Type extends CommandSender> extends Kind<Type> {

    public static final CommandSenderKind<CommandSender> COMMAND_SENDER = new CommandSenderKind<>();

    @SuppressWarnings("unchecked")
    public CommandSenderKind() {
        this((Class<Type>) CommandSender.class);
    }

    protected CommandSenderKind(Class<Type> type) {
        super(type);
    }

    @Override
    public Object getProperty(Type thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "name" -> thing.getName();
            case "type" -> this;
            case "has_permission" -> CheckedFunction.of(STRING).runs(thing::hasPermission);
            case "send_message" ->
                new InternalStatement((context, arguments) -> this.sendMessage(thing, context, arguments));
            case "is_op" -> thing.isOp();
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"name", "type", "has_permission", "send_message", "is_op"};
    }

    @Override
    public String toString(Type sender) {
        if (sender instanceof Player player) return player.getName();
        else return "Console";
    }

    public Object sendMessage(CommandSender target, Context context, Arguments arguments) {
        final AbstractScript script = context.data().script;
        final Component component = Component.text(Objects.toString(arguments.get(0)))
            .hoverEvent(
                Component.text("Sent by " + this.toStringTry(context.source()) + " (from " + script.name() + ")"));
        target.sendMessage(component);
        return true;
    }

}
