package mx.kenzie.craftscript.kind;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.core.InternalStatement;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandSenderKind extends Kind<CommandSender> {

    public static final CommandSenderKind COMMAND_SENDER = new CommandSenderKind();

    public CommandSenderKind() {
        super(CommandSender.class);
    }

    @SuppressWarnings("unchecked")
    protected CommandSenderKind(Class<? extends CommandSender> type) {
        super((Class<CommandSender>) type);
    }

    @Override
    public Object getProperty(CommandSender thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "name" -> thing.getName();
            case "type" -> this;
            case "has_permission" ->
                new InternalStatement(arguments -> thing.hasPermission(Objects.toString(arguments.get(0))));
            case "send_message" ->
                new InternalStatement((context, arguments) -> this.sendMessage(thing, context, arguments));
            case "is_op" -> thing.isOp();
            default -> null;
        };
    }

    @Override
    public Object setProperty(CommandSender thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.asKind(value));
            case "equals" -> thing.equals(value);
            case "has_permission" -> thing.hasPermission((String) value);
            default -> null;
        };
    }

    @Override
    public String[] getProperties() {
        return new String[]{"name", "type", "has_permission", "send_message", "is_op"};
    }

    @Override
    public String toString(CommandSender sender) {
        if (sender instanceof Player player) return player.getName();
        else return "Console";
    }

    public Object sendMessage(CommandSender target, Context context, Arguments arguments) {
        final AbstractScript script = context.data().script;
        final Component component = Component.text(Objects.toString(arguments.get(0)))
            .hoverEvent(Component.text("Sent by " + this.toString(context.source()) + " (from " + script.name() + ")"));
        target.sendMessage(component);
        return true;
    }

}
