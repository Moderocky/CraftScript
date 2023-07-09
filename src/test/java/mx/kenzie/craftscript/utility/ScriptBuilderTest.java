package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.TestCommandSender;
import mx.kenzie.craftscript.TestPrintCommand;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.kind.Kinds;
import mx.kenzie.craftscript.script.*;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Objects;

import static mx.kenzie.craftscript.utility.ScriptBuilder.*;

public class ScriptBuilderTest {

    private static ScriptManager manager;
    private final TestCommandSender sender = new TestCommandSender();

    @BeforeClass
    public static void startup() {
        manager = new ScriptManager(null, ScriptLoader.BASIC);
        for (final Kind<?> kind : Kinds.kinds) manager.registerKind(kind);
        manager.loadScript(Libraries.MATH);
        manager.loadScript(Libraries.PARSER);
        manager.loadScript(Libraries.GLOBAL);
        manager.loadScript(Libraries.DEBUG);
        manager.loadScript(Libraries.REFLECTION);
    }

    @Test
    public void simple() {
        final AbstractScript script = script();
        assert script.statements().length == 0;
    }

    @Test
    public void basic() {
        final AbstractScript script = script(line(set(var("test"), literal(1))), line(var("test")));
        assert this.test(script, 1);
    }

    @Test
    public void lines() {
        final AbstractScript script = script(line(set(var("test"), literal(1))),
            line(if_(is(var("test"), literal(1)), block(line(set(var("test"), literal("yes")))))), line(var("test")));
        assert this.test(script, "yes");
    }

    private boolean test(AbstractScript script, Object output) {
        final Context.Data data = new Context.Data();
        data.localCommands.add(new TestPrintCommand());
        final Context context = new Context(sender, manager, new VariableContainer(), data);
        Context.setLocalContext(context);
        try {
            final Object result = Wrapper.unwrap(script.execute(context));
            assert Objects.equals(result, output) : result;
            return true;
        } finally {
            Context.removeLocalContext();
        }
    }

}
