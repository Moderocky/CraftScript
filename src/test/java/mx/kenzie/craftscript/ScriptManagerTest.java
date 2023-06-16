package mx.kenzie.craftscript;


import mx.kenzie.craftscript.kind.*;
import mx.kenzie.craftscript.parser.*;
import mx.kenzie.craftscript.variable.VariableContainer;
import org.bukkit.command.CommandSender;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Objects;

public class ScriptManagerTest {

    private static final ScriptLoader LOADER = new SimpleScriptLoader(
        NullParser::new,
        ForParser::new,
        IfParser::new,
        InvertParser::new,
        BlockParser::new,
        ListParser::new,
        StringParser::new,
        KindParser::new,
        CloseParser::new,
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
        manager.registerKind(new LocationKind());
        manager.registerKind(new VectorKind());
        manager.registerKind(new ListKind());
        manager.registerKind(new CollectionKind());
        manager.registerKind(new KindKind());
    }

    @AfterClass
    public static void tearDown() {
        manager.close();
        manager = null;
    }

    public static void main(String[] args) throws Throwable {
        startup();
        final CommandSender sender = new OutputCommandSender();
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestPrintCommand());
        final Context context = new Context(sender, manager, new VariableContainer(), data);
        Context.setLocalContext(context);
        final Script script = manager.loadScript("test.csc", """
            if "baefell"[equals="baefell"] {
               if "baefell"[equals="baefell"] {
                 if "baefell"[equals="baefell"] {
                   /print hi im baefell
                 }
               }
             }
            """);
        script.execute(context);
        tearDown();
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

    @Test
    public void ifCheck() {
        assert this.test("""
            var = "hello"
            /print char at 1 is {var[char_at=1]}
            if var {
                /print char at 2 is {var[char_at=2]}
            }
            """, "char at 2 is l");
    }

    @Test
    public void ifChangeVar() {
        assert this.test("""
            var = "hello"
            if var {
                var = "{var} there"
            }
            /print {var}
            """, "hello there");
    }

    @Test
    public void ifSetterTest() {
        assert this.test("""
            var = "hello"
            if var[length=5] {
                var = "{var} there"
            }
            /print {var}
            """, "hello there");
    }

    @Test
    public void forTest() {
        assert this.test("""
            for var = "hello there" {
                /print {var}
            }
            """, "hello there");
    }

    @Test
    public void forListTest() {
        assert this.test("""
            string = "test"
            for var = ["hello there", "general kenobi"] {
                string = "{string} {var}"
            }
            /print {string}
            """, "test hello there general kenobi");
    }

    @Test
    public void ifKindTest() {
        assert this.test("""
            var = "no"
            if var[type=#string] {
                var = "yes"
            }
            /print {var}
            """, "yes");
    }

    @Test
    public void kindTest() {
        assert this.test("""
            /print {"hello"[type]}
            """, "#string");
    }

    @Test
    public void junk() {
        assert this.test("""
            text = "hello"
            if text[length=5] {
              /print the word "{text}" is {text[length]} letters!
            }
            name = "BaeFell"
            text = "hi im {name}"
            /print {text}
            /print hi {name} im phil
            """, "hi BaeFell im phil");
    }

    @Test
    public void equality() {
        assert this.test("""
            if "baefell"[equals="baefell"] {
               if "baefell"[equals="baefell"] {
                 if "baefell"[equals="baefell"] {
                   /print hi im baefell
                 }
               }
             }
            """, "hi BaeFell im phil");
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
