package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.TestPrintCommand;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import org.junit.BeforeClass;

import java.util.Objects;

public class SpeedTest extends CompilerTest {

    @BeforeClass
    public static void startup() {
        CompilerTest.startup();
        compiler = new SubstantiveScriptCompiler();
    }

    public static void main(String[] args) throws Throwable {
        final SpeedTest test = new SpeedTest();
        SpeedTest.startup();
        test.speed();
        SpeedTest.tearDown();
    }

    private void speed() throws Throwable {
        final int revs = 1000000;
        final String result = "hi BaeFell im phil";
        final AbstractScript interpreted = this.parse("speed.script", """
            result = null
            text = "hello"
            if text[length=5] {
              result = "the word '{text}' is {text[length]} letters!"
            }
            name = "BaeFell"
            text = "hi im " + name
            result = text
            result = "hi {name} im phil"
            result
            """);
        this.compile(interpreted);
        final CompiledScript compiled = (CompiledScript) this.load(interpreted).getConstructor().newInstance();
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestPrintCommand());
        final Context context = new Context(sender, manager, new VariableContainer(), data);
        Context.setLocalContext(context);
        for (int i = 0; i < 50; i++) {
            if (!Objects.equals(this.run(interpreted, context), result)) throw new Error();
            if (!Objects.equals(this.run(compiled, context), result)) throw new Error();
        }
        {
            final long start = System.currentTimeMillis(), end;
            for (int i = 0; i < revs; i++) {
                if (!Objects.equals(this.run(interpreted, context), result)) throw new Error();
            }
            end = System.currentTimeMillis();
            System.out.println("Interpreted script took " + (end - start) + "ms for " + revs + " runs.");
        }
        {
            final long start = System.currentTimeMillis(), end;
            for (int i = 0; i < revs; i++) {
                if (!Objects.equals(this.run(compiled, context), result)) throw new Error();
            }
            end = System.currentTimeMillis();
            System.out.println("Compiled script took " + (end - start) + "ms for " + revs + " runs.");
        }
        Context.removeLocalContext();
    }


    private Object run(AbstractScript script, Context context) {
        return Wrapper.unwrap(script.execute(context));
    }

}
