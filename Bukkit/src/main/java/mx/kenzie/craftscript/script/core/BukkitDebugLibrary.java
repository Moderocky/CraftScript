package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.command.ScriptArgument;
import mx.kenzie.craftscript.utility.Executable;
import mx.kenzie.craftscript.utility.PrettyPrinter;
import mx.kenzie.craftscript.variable.Wrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class BukkitDebugLibrary extends DebugLibrary {

    private static final Executable<?> LINE = context -> {
        final ColorProfile profile = ScriptArgument.COLOR_PROFILE;
        final CommandSender sender = context.getSource();
        sender.sendMessage(Component.textOfChildren(
            Component.text("Current Line:", profile.dark())
        ));
        sender.sendMessage(Component.textOfChildren(
            Component.text("File '", profile.dark()),
            Component.text(context.data().script.name(), profile.highlight()),
            Component.text("' line ", profile.dark()),
            Component.text(context.getLine(), profile.highlight())
        ));
        try {
            sender.sendMessage(context.data().line.prettyPrint(profile));
        } finally {
            PrettyPrinter.reset();
        }
        return null;
    };
    private static final Executable<?> SCRIPT = context -> {
        final ColorProfile profile = ScriptArgument.COLOR_PROFILE;
        final CommandSender sender = context.getSource();
        sender.sendMessage(Component.textOfChildren(
            Component.text("Current Script:", profile.dark())
        ));
        sender.sendMessage(Component.textOfChildren(
            Component.text("File '", profile.dark()),
            Component.text(context.data().script.name(), profile.highlight()),
            Component.text("'", profile.dark())
        ));
        try {
            sender.sendMessage(context.data().script.prettyPrint(profile));
        } finally {
            PrettyPrinter.reset();
        }
        return null;
    };
    private static final Executable<?> VARIABLES = context -> {
        final ColorProfile profile = ScriptArgument.COLOR_PROFILE;
        final CommandSender sender = context.getSource();
        sender.sendMessage(Component.textOfChildren(
            Component.text("Current Variable Structure:", profile.dark())
        ));
        sender.sendMessage(Component.textOfChildren(
            Component.text("File '", profile.dark()),
            Component.text(context.data().script.name(), profile.highlight()),
            Component.text("' line ", profile.dark()),
            Component.text(context.getLine(), profile.highlight())
        ));
        for (final Entry<String, Object> entry : context.variables().entrySet()) {
            sender.sendMessage(Component.textOfChildren(
                Component.text(entry.getKey(), profile.highlight()),
                Component.text(": ", profile.pop()),
                Component.text(Wrapper.of(entry.getValue()).toString(), profile.light())
            ));
        }
        return null;
    };

    public BukkitDebugLibrary() {
        super("variables", "line", "script");
    }

    @Override
    public Object get(String key) {
        return switch (key) {
            case "variables" -> VARIABLES;
            case "line" -> LINE;
            case "script" -> SCRIPT;
            default -> null;
        };
    }

}
