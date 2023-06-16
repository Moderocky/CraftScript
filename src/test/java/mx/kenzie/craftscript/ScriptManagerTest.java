package mx.kenzie.craftscript;


import mx.kenzie.craftscript.kind.*;
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
        manager.registerKind(new StringKind());
        manager.registerKind(new PlayerKind());
        manager.registerKind(new CommandSenderKind());
        manager.registerKind(new NumberKind());
        manager.registerKind(new NullKind());
    }

    @AfterClass
    public static void tearDown() {
        manager.close();
        manager = null;
    }

    @Test
    public void simple() {
        assert this.test(this.getScript("simple.csc"), "hello BaeFell was true");
    }

    @Test
    public void variableInterpolation() {
        assert this.test("""
            var = "hello"
            /print {var}
            """, "hello");
    }

    @Test
    public void noInterpolation() {
        assert this.test("""
            var = "hello"
            /print var
            """, "var");
    }

    @Test
    public void simpleGetter() {
        assert this.test("""
            var = "hello"
            /print string is {var[length]} chars
            """, "string is 5 chars");
    }

    @Test
    public void setterTrick() {
        assert this.test("""
            var = "hello"
            /print char at 1 is {var[char_at=1]}
            """, "char at 1 is e");
    }

    private boolean test(String source, String output) {
        final Script script = manager.loadScript("test.csc", source);
        try {
            this.test(script, output);
        } finally {
            manager.deleteScript(script);
        }
        return true;
    }

    private boolean test(InputStream source, String output) {
        final Script script = manager.loadScript("test.csc", source);
        try {
            this.test(script, output);
        } finally {
            manager.deleteScript(script);
        }
        return true;
    }

    private boolean test(Script script, String output) {
        final TestCommandSender sender = new TestCommandSender();
        assert Objects.equals(sender.output, null) : sender.output;
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestPrintCommand());
        final Context context = new Context(sender, manager, new VariableContainer(), data);
        Context.setLocalContext(context);
        try {
            if (!script.execute(context) || !Objects.equals(sender.output, output)) {
                script.debug(System.err);
                assert false : sender.output;
            }
        } finally {
            Context.removeLocalContext();
        }
        return true;
    }

    private InputStream getScript(String name) {
        return ScriptManagerTest.class.getClassLoader().getResourceAsStream(name);
    }

}
