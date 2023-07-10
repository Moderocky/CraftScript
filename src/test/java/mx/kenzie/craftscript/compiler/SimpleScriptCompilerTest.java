package mx.kenzie.craftscript.compiler;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class SimpleScriptCompilerTest extends CompilerTest {

    @BeforeClass
    public static void startup() {
        CompilerTest.startup();
        compiler = new SimpleScriptCompiler();
    }

    @AfterClass
    public static void tearDown() {
        CompilerTest.tearDown();
    }

}
