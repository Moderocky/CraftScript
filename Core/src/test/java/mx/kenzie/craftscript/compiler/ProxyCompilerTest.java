package mx.kenzie.craftscript.compiler;

import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptManager;
import org.junit.Test;

import java.util.Objects;

public class ProxyCompilerTest {

    @Test
    public void simple() throws Throwable {
        final Context<?> context = Context.system();
        final ScriptManager<?> manager = context.manager();
        final AbstractScript script = manager.parseScript("""
            name = "Jeremy"
            """);
        final Object result = script.execute(context);
        assert result.equals("Jeremy");
        final Object thing = ProxyCompiler.of(Object.class).create(context.variables());
        assert thing != null;
        final Class<?> type = thing.getClass();
        assert type != Object.class;
        assert type.getSuperclass() == Object.class;
        assert type.getFields().length == 1;
        assert type.getField("name").get(thing).equals("Jeremy");
    }

    @Test
    public void withFunction() {
        final Context<?> context = Context.system();
        final ScriptManager<?> manager = context.manager();
        final AbstractScript script = manager.parseScript("""
            name = fn() {
                "Jeremy"
            }
            """);
        script.execute(context);
        final Thing1 thing = ProxyCompiler.<Thing1>of(Object.class, Thing1.class).create(context.variables());
        assert thing != null;
        final Class<?> type = thing.getClass();
        assert type.getSuperclass() == Object.class;
        assert type.getFields().length == 1;
        assert thing.name().equals("Jeremy");
    }

    @Test
    public void withFunctionArgs() {
        final Context<?> context = Context.system();
        final ScriptManager<?> manager = context.manager();
        final AbstractScript script = manager.parseScript("""
            name = fn(text) {
                "Jeremy " + text
            }
            """);
        script.execute(context);
        final Thing2 thing = ProxyCompiler.<Thing2>of(Object.class, Thing2.class).create(context.variables());
        assert thing != null;
        final Class<?> type = thing.getClass();
        assert type.getSuperclass() == Object.class;
        assert type.getFields().length == 1;
        assert thing.name("Bearimy").equals("Jeremy Bearimy");
    }

    @Test
    public void withClass() {
        final ScriptManager<?> manager = Context.system().manager();
        final AbstractScript script = manager.parseScript("""
            foo = "there"
            hello = fn(text) {
                "hello " + text
            }
            """);
        final Blob thing = ProxyCompiler.<Blob>of(Blob.class).create(script);
        assert thing != null;
        assert thing.hello("there").equals("hello there");
        assert Objects.equals(thing.foo, "there");
        assert thing.test().equals("hello there");
        assert thing.getClass().getSuperclass() == Blob.class;
    }

    public interface Thing1 {

        String name();

    }

    public interface Thing2 {

        String name(String text);

    }

    public static class Blob {

        public String foo;

        public String hello(String text) {
            return "hello world";
        }

        public String test() {
            return "hello " + foo;
        }

    }

}