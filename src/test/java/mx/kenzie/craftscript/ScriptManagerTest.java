package mx.kenzie.craftscript;


import mx.kenzie.craftscript.kind.*;
import mx.kenzie.craftscript.script.*;
import mx.kenzie.craftscript.variable.VariableContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Objects;

public class ScriptManagerTest {

    static ScriptManager manager;
    private final TestCommandSender sender = new TestCommandSender();

    @BeforeClass
    public static void startup() {
        manager = new ScriptManager(null, ScriptLoader.BASIC);
        manager.registerKind(new StringKind());
        manager.registerKind(new PlayerKind());
        manager.registerKind(new CommandSenderKind());
        manager.registerKind(new NumberKind());
        manager.registerKind(new NullKind());
        manager.registerKind(new LocationKind());
        manager.registerKind(new VectorKind());
        manager.registerKind(new BlockDataKind());
        manager.registerKind(new BlockKind());
        manager.registerKind(new EventKind());
        manager.registerKind(new StatementKind());
        manager.registerKind(new ExecutableKind());
        manager.registerKind(new MapKind());
        manager.registerKind(new ListKind());
        manager.registerKind(new CollectionKind());
        manager.registerKind(new KindKind());
        manager.loadScript(Libraries.MATH);
        manager.loadScript(Libraries.PARSER);
        manager.loadScript(Libraries.GLOBAL);
        manager.loadScript(Libraries.DEBUG);
        manager.loadScript(Libraries.REFLECTION);
    }

    @AfterClass
    public static void tearDown() {
        manager.close();
        manager = null;
    }

    @Test
    public void variablePassingTest() {
        assert this.test("""
            import [reflection]
            var = 10
            func = function {
                /print {var}
            }
            variables = run reflection[variables]
            run func 5
            run func variables
            """, "10");
    }

    @Test
    public void reflectionTest() {
        assert this.test("""
            import [reflection]
            var = 10
            variables = run reflection[variables]
            variables[var=5]
            /print {var}
            """, "5");
        assert this.test("""
            import [reflection]
            var = 10
            script = run reflection[script]
            /print {script}
            """, "test.script");
    }

    @Test
    public void eventListenerTest() {
        final Script script = manager.loadScript("test.script", """
            on test:event {
                /print hello
            }
            """);
        assert this.test(script, null);
        assert Objects.equals(sender.output, null);
        manager.emit(new TestEvent());
        assert Objects.equals(sender.output, "hello");
        manager.deleteScript(script);
        this.sender.output = null;
        manager.emit(new TestEvent());
        assert Objects.equals(sender.output, null);
    }

    @Test
    public void resourceKeyTest() {
        assert this.test("""
            key = minecraft:test
            /print {key[type]}
            """, "#namespacedkey");
        assert this.test("""
            key = minecraft:test
            /print {key}
            """, "minecraft:test");
    }

    @Test
    public void elseTest() {
        assert this.test("""
            if 5 > 6 {
                /print yes
            } else {
                /print no
            }
            """, "no");
        assert this.test("""
            if 5 > 3 {
                /print yes
            } else {
                /print no
            }
            """, "yes");
        assert this.test("""
            /print bean
            if 5 > 6 {
                /print yes
            } else {
            }
            """, "bean");
    }

    @Test
    public void whileTest() {
        assert this.test("""
            var = 0
            while var < 5 var = var + 1
            /print {var}
            """, "5");
        assert this.test("""
            var = 5
            while var > 0 {
                var = var - 1
            }
            /print {var}
            """, "0");
    }

    @Test
    public void parserImportTest() {
        assert this.test("""
            import [parser]
            script = run parser "/print hello"
            run script
            """, "hello");
    }

    @Test
    public void globalTest() {
        assert !manager.getGlobalVariables().containsKey("test");
        assert this.test("""
            import [global]
            assert global[test] == null
            global[test="hello"]
            """);
        assert manager.getGlobalVariables().containsKey("test");
        assert this.test("""
            import [global]
            assert global[test] != null
            assert global[test] == "hello"
            """);
        manager.getGlobalVariables().remove("test");
        assert this.test("""
            import [global]
            assert global[test] == null
            """);
        assert !manager.getGlobalVariables().containsKey("test");
    }

    @Test
    public void importTest() {
        assert this.test("""
            import [math]
            result = run math[floor] 10.5
            assert result == 10
            result = run math[abs] -5
            /print {result}
            """, "5");
        final Script script = manager.loadScript("other.script", """
            struct {
                test = function {
                    /print hello there!
                }
            }
            """);
        assert this.test("""
            import [other]
            run other[test]
            """, "hello there!");
        manager.deleteScript(script);
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
            b = run script ["general kenobi"]
            /print {a} {b}
            """, "hello there general kenobi");
        manager.deleteScript(script);
    }

    @Test
    public void functionTest() {
        assert this.test("""
            func = function blob
            /print {run func}
            """, "null");
        assert this.test("""
            blob = "hello world"
            func = function blob
            /print {run func}
            """, "null");
        assert this.test("""
            func = function blob
            /print {run func [blob="hello there"]}
            """, "hello there");
        assert this.test("""
            run function {
                /print general kenobi
            }
            """, "general kenobi");
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
            result = var = "hello"
            if var == result {
                /print {result}
            }
            """, "hello");
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
    public void comparators() {
        assert this.test("/print {9 < 10}", "true");
        assert this.test("/print {10 < 10}", "false");
        assert this.test("/print {9 <= 10}", "true");
        assert this.test("/print {10 <= 10}", "true");
        assert this.test("/print {11 <= 10}", "false");
        assert this.test("/print {9 < 10}", "true");
        assert this.test("assert 10 > 1");
        assert this.test("assert 10 >= 1");
        assert this.test("assert 10 >= 10");
        assert this.test("assert true & true");
        assert this.test("assert true | false");
        assert this.test("assert false | true");
        assert this.test("assert false ^ true");
        assert this.test("assert null ? true");
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

    private boolean test(String script) {
        return this.test(script, null);
    }

    private boolean test(Script script, String output) {
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestPrintCommand());
        final Context context = new Context(sender, manager, new VariableContainer(), data);
        Context.setLocalContext(context);
        try {
            script.execute(context);
            if (output != null && !Objects.equals(sender.output, output)) {
                script.debug(System.err);
                assert false : sender.output;
            }
        } finally {
            Context.removeLocalContext();
            this.sender.output = null;
        }
        return true;
    }

    private InputStream getScript(String name) {
        return ScriptManagerTest.class.getClassLoader().getResourceAsStream(name);
    }

}
