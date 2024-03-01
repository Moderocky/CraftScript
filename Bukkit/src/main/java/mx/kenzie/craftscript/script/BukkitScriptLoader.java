package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.*;

import java.util.function.Supplier;

public class BukkitScriptLoader extends SimpleScriptLoader {

    public static final ScriptLoader BASIC = new BukkitScriptLoader(
        SimpleScriptLoader.BASIC,
        ListenerParser::new,
        SelectorParser::new,
        CommandParser::new,
        ResourceParser::new
    );

    @SafeVarargs
    public BukkitScriptLoader(SimpleScriptLoader loader, Supplier<Parser>... parsers) {
        super(loader, parsers);
    }

    @SafeVarargs
    public BukkitScriptLoader(Supplier<Parser>... parsers) {
        super(parsers);
    }

}
