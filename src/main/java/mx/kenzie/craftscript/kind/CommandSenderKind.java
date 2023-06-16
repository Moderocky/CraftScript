package mx.kenzie.craftscript.kind;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandSenderKind extends Kind<CommandSender> {

    public CommandSenderKind() {
        super(CommandSender.class);
    }

    @Override
    public Object getProperty(CommandSender thing, String property) {
        if (thing == null) return null;
        return switch (property) {
            case "name" -> thing.getName();
            case "type" -> this;
            default -> null;
        };
    }

    @Override
    public Object setProperty(CommandSender thing, String property, Object value) {
        if (thing == null) return null;
        return switch (property) {
            case "type" -> this.equals(Kind.of(value));
            case "equals" -> thing.equals(value);
            case "has_permission" -> thing.hasPermission((String) value);
            default -> null;
        };
    }

    @Override
    public CommandSender fromString(String string) {
        final CommandSender sender = Bukkit.getPlayer(string);
        if (sender == null) return Bukkit.getConsoleSender();
        return sender;
    }

}
