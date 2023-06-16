package mx.kenzie.craftscript;


import mx.kenzie.craftscript.parser.*;
import mx.kenzie.craftscript.variable.VariableContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Objects;

public class ScriptManagerTest {

    private static final ScriptLoader LOADER = new SimpleScriptLoader(
        NullParser::new,
        StringParser::new,
        IntegerParser::new,
        DoubleParser::new,
        CommandParser::new,
        SetterParser::new,
        GetterParser::new,
        InterpolationParser::new,
        VariableAssignmentParser::new,
        VariableParser::new
    );

    private static ScriptManager manager;

    @BeforeClass
    public static void startup() {
        manager = new ScriptManager(null, LOADER);
    }

    @Test
    public void simple() {
        final Script script = manager.loadScript("simple.csc", this.getScript("simple.csc"));
        final TestCommandSender sender = new TestCommandSender();
        assert Objects.equals(sender.output, null) : sender.output;
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestSayCommand());
        final Context context = new Context(sender, manager, new VariableContainer(), data);
        assert script.execute(context);
        assert Objects.equals(sender.output, "hello BaeFell was true") : sender.output;
    }

    @AfterClass
    public static void tearDown() {
        manager.close();
        manager = null;
    }

    private InputStream getScript(String name) {
        return ScriptManagerTest.class.getClassLoader().getResourceAsStream(name);
    }

}
