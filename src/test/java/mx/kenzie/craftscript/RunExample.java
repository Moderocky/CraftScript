package mx.kenzie.craftscript;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.script.ScriptManager;
import mx.kenzie.craftscript.variable.VariableContainer;

import java.io.InputStream;

public class RunExample {

    public static void main(String[] args) {
        ScriptManagerTest.startup();
        final ScriptManager manager = ScriptManagerTest.manager;
        final InputStream stream = RunExample.class.getClassLoader().getResourceAsStream("example.script");
        final Script script = manager.loadScript("example.script", stream);
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestPrintCommand());
        final OutputCommandSender sender = new OutputCommandSender();
        script.execute(new Context(sender, manager, new VariableContainer(), data));
        ScriptManagerTest.tearDown();
    }

}
