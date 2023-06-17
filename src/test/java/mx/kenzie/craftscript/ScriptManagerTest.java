package mx.kenzie.craftscript;


import mx.kenzie.craftscript.kind.*;
import mx.kenzie.craftscript.parser.ScriptParser;
import mx.kenzie.craftscript.parser.*;
import mx.kenzie.craftscript.script.*;
import mx.kenzie.craftscript.variable.VariableContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Objects;

public class ScriptManagerTest {

    private static final ScriptLoader LOADER = new SimpleScriptLoader(
        NullParser::new,
        AssertParser::new,
        ForParser::new,
        IfParser::new,
        StructParser::new,
        RequireParser::new,
        RunParser::new,
        InvertParser::new,
        BooleanParser::new,
        SelectorParser::new,
        ScriptParser::new,
        BlockParser::new,
        MapParser::new,
        ListParser::new,
        StringParser::new,
        InterpolationParser::new,
        KindParser::new,
        CloseParser::new,
        IntegerParser::new,
        DoubleParser::new,
        CommandParser::new,
        VariableAssignmentParser::new,
        EqualsParser::new,
        PlusParser::new,
        MinusParser::new,
        TimesParser::new,
        DivideParser::new,
        SetterParser::new,
        GetterParser::new,
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
        manager.registerKind(new BlockDataKind());
        manager.registerKind(new BlockKind());
        manager.registerKind(new ListKind());
        manager.registerKind(new CollectionKind());
        manager.registerKind(new KindKind());
    }

    @AfterClass
    public static void tearDown() {
        manager.close();
        manager = null;
    }

    @Test
    public void scriptLiteral() {
        final Script script = manager.loadScript("blob.script", "");
        assert this.test("""
            script = blob.script
            /print {script}
            """, "blob.script");
        manager.deleteScript(script);
    }

    @Test
    public void scriptRun() {
        final Script script = manager.loadScript("blob.script", """
            require [text]
            text
            """);
        assert this.test("""
            a = run blob.script [text="hello there"]
            script = blob.script
            b = run script [text="general kenobi"]
            /print {a} {b}
            """, "hello there general kenobi");
        manager.deleteScript(script);
    }

    @Test
    public void assertCheck() {
        assert this.test("""
            three = 1
            three = three + 2
            assert three == 3
            """, null);
    }

    @Test(expected = ScriptError.class)
    public void assertCheckFail() {
        assert this.test("""
            three = 1
            three = three + 2
            assert three == 2
            """, null);
    }

    @Test
    public void maths() {
        assert this.test("""
            three = 1
            three = three + 2
            result = var == "hello"
            if three == 3 {
                result = "{three}"
            }
            five = 8 - 3
            if five == 5 {
                result = "{result} {five}"
            }
            five = 10 / 2
            if five == 5 {
                result = "{result} {five}"
            }
            seven = 3.5 * 2
            if seven == 7 {
                result = "{result} {seven}"
            }
            /print {result}
            """, "3 5 5 7.0");
    }

    @Test
    public void equalsCheck() {
        assert this.test("""
            var = "hello"
            result = var == "hello"
            if var == result {
                /print {result}
            }
            """, "true");
    }

    @Test
    public void selectorCheck() {
        assert this.test("""
            /print empty
            for var = @a {
                /print {var}
            }
            """, "empty");
        assert this.test("""
            /print empty
            for var = @s {
                /print {var}
            }
            """, "Console");
    }

    @Test
    public void simple() {
        assert this.test(this.getScript("simple.script"), "hello BaeFell was true");
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
    public void nestTest() {
        assert this.test("""
            var = var = "hello"
            /print {var} there
            """, "hello there");
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
            """, "hi im baefell");
    }

    @Test
    public void typeDeclaration() {
        assert this.test("""
            person = struct {
                name = "BaeFell"
                age = 42
                height = 1.8
            }
            /print {person[name]} is {person[age]} years old and {person[height]}m tall
            """, "BaeFell is 42 years old and 1.8m tall");
    }

    @Test
    public void requireVariables() {
        assert this.test("""
            require[]
            """, null);
    }

    @Test(expected = ScriptError.class)
    public void requireVariablesFail() {
        assert this.test("""
            require[bee, spoon]
            """, null);
    }

    private boolean test(String source, String output) {
        final Script script = manager.loadScript("test.script", source);
        try {
            this.test(script, output);
        } finally {
            manager.deleteScript(script);
        }
        return true;
    }

    private boolean test(InputStream source, String output) {
        final Script script = manager.loadScript("test.script", source);
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
            script.execute(context);
            if (!Objects.equals(sender.output, output)) {
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
