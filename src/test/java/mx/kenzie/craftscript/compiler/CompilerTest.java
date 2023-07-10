package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.TestCommandSender;
import mx.kenzie.craftscript.TestPrintCommand;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.kind.Kinds;
import mx.kenzie.craftscript.script.*;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import mx.kenzie.foundation.UnloadedClass;
import org.jetbrains.annotations.Contract;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public abstract class CompilerTest {

    protected static final File folder = new File("target/test-scripts/");
    protected static ScriptManager manager;
    protected static ScriptCompiler compiler;
    protected final TestCommandSender sender = new TestCommandSender();

    @BeforeClass
    public static void startup() {
        manager = new ScriptManager(null, ScriptLoader.BASIC);
        for (final Kind<?> kind : Kinds.kinds) manager.registerKind(kind);
        manager.loadScript(Libraries.MATH);
        manager.loadScript(Libraries.PARSER);
        manager.loadScript(Libraries.GLOBAL);
        manager.loadScript(Libraries.DEBUG);
        manager.loadScript(Libraries.REFLECTION);
        folder.mkdirs();
    }

    @AfterClass
    public static void tearDown() {
        manager.close();
        manager = null;
        compiler = null;
    }

    @Test
    public void simplePrint() {
        assert this.test("""
            /print hello
            """, "hello");
    }

    @Test
    public void basic() throws Throwable {
        final AbstractScript script = this.parse("basic", """
            var = 5
            """);
        assert this.compile(script);
        final Class<?> type = this.load(script);
        final Object result = this.run(type);
        assert Objects.equals(result, 5) : result + " (" + result.getClass().getName() + ')';
    }

    @Test
    public void block() throws Throwable {
        final AbstractScript script = this.parse("block", """
            var = 5
            if var > 4 {
                var = var + 4
            }
            var
            """);
        assert this.compile(script);
        final Class<?> type = this.load(script);
        final Object result = this.run(type);
        assert Objects.equals(result, 9) : result + " (" + result.getClass().getName() + ')';
    }

    @Test
    public void ifSetterTest() {
        assert this.test("""
            var = "hello"
            if var[length] == 5 {
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
            if var[type] == #string {
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
    public void equals() {
        assert this.test("""
            if "baefell" == "baefell" null
            """, "hi im baefell");
    }

    @Test
    public void equality() {
        assert this.test("""
            if "baefell" == "baefell" {
               if "baefell"[lowercase] == "baefell" {
                 if "baefell"[uppercase] == "BAEFELL" {
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

    protected AbstractScript parse(String name, String content) {
        return manager.parseScript(name + ".script", content);
    }

    protected boolean test(String source) {
        final StackTraceElement[] elements = new Throwable().getStackTrace();
        final StackTraceElement element = elements[1];
        final String name = element.getMethodName();
        final AbstractScript script = this.parse(name, source);
        try {
            assert this.compile(script);
            final Class<?> type = this.load(script);
            final Object result = this.run(type);
        } catch (Throwable ex) {
            throw new ScriptCompileError("Unknown error.", ex);
        } finally {
            this.clearOutput();
        }
        return true;

    }

    protected boolean test(String source, String output) {
        final StackTraceElement[] elements = new Throwable().getStackTrace();
        final StackTraceElement element = elements[1];
        final String name = element.getMethodName();
        final AbstractScript script = this.parse(name, source);
        try {
            assert this.compile(script);
            final Class<?> type = this.load(script);
            final Object result = this.run(type);
            assert Objects.equals(sender.output, output) : sender.output;
        } catch (Throwable ex) {
            throw new ScriptCompileError("Unknown error.", ex);
        } finally {
            this.clearOutput();
        }
        return true;
    }

    @Contract(pure = true)
    protected void clearOutput() {
        sender.output = null;
    }

    protected boolean compile(AbstractScript script) throws Throwable {
        final File file = new File(folder, script.name().substring(0, script.name().indexOf('.')) + ".class");
        if (!file.exists()) file.createNewFile();
        compiler.compile(script, file);
        return true;
    }

    protected Class<?> load(AbstractScript script) {
        final UnloadedClass unloaded = compiler.prepare(script);
        assert unloaded != null;
        return unloaded.load(new TestClassLoader());
    }

    protected Object run(Class<?> type) throws Throwable {
        final CompiledScript script = (CompiledScript) type.getConstructor().newInstance();
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestPrintCommand());
        final Context context = new Context(sender, manager, new VariableContainer(), data);
        Context.setLocalContext(context);
        try {
            return Wrapper.unwrap(script.execute(context));
        } finally {
            Context.removeLocalContext();
        }
    }

}
