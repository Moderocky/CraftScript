package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.AbstractScript;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class SubstantiveScriptCompilerTest extends CompilerTest {

    @BeforeClass
    public static void startup() {
        CompilerTest.startup();
        compiler = new SubstantiveScriptCompiler();
    }

    @AfterClass
    public static void tearDown() {
        CompilerTest.tearDown();
    }

    @Test
    public void checkFilesOk() {
        final File source = new File("src/main/java/mx/kenzie/craftscript/");
        final File statements = new File(source, "statement/"), elements = new File(source, "compiler/element/");
        assert statements.isDirectory();
        assert elements.isDirectory();
    }

    @Test
    public void blob() throws Throwable {
        final AbstractScript script = this.parse("basic", """
            var = "hello"
            var = 5
            var
            """);
        assert this.compile(script);
        final Class<?> type = this.load(script);
        final Object result = this.run(type);
        assert Objects.equals(result, 5) : result + " (" + result.getClass().getName() + ')';
    }

    @Test
    public void interpolations() throws Throwable {
        this.test("""
            /print hello
            """, "hello");
        this.test("""
            var = 5
            /print {var}
            """, "5");
        this.test("""
            var = "there"
            var = "hello {var}"
            /print {var}
            """, "hello there");
    }

    @Test
    public void function() throws Throwable {
        this.test("""
            var = function 10
            run var
            """);
    }

    @Test
    public void ifBlock() throws Throwable {
        assert this.test("""
            var = 1
            if true var = 2
            var
            """).equals(2);
        assert this.test("""
            var = 1
            if false {
                var = 2
            } else {
                var = 5
            }
            var
            """).equals(5);
        assert this.test("""
            var = 1
            if var == 1 {
                var = 2
            }
            var
            """).equals(2);
    }

}
