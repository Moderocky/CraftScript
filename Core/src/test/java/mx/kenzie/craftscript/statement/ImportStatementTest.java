package mx.kenzie.craftscript.statement;

import mx.kenzie.craftscript.ManagedTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Objects;

public class ImportStatementTest extends ManagedTest {

    @BeforeClass
    public static void setup() {
        manager.loadScript("testing.script", """
            first = "hello"
            second = "there"
            "blob"
            """);
    }

    @AfterClass
    public static void teardown() {
        manager.deleteScript("testing.script");
    }

    @Test
    public void script() {
        final Object result = this.run("""
            import [testing]
            testing
            """);
        assert Objects.equals(result, "blob") : result;
    }

    @Test
    public void variable() {
        final Object result = this.run("""
            import [first from testing]
            first
            """);
        assert Objects.equals(result, "hello") : result;
    }

    @Test
    public void variables() {
        final Object result = this.run("""
            import [first from testing, second from testing]
            first + " " + second
            """);
        assert Objects.equals(result, "hello there") : result;
    }

    @Test
    public void star() {
        final Object result = this.run("""
            import [* from testing]
            first + " " + second
            """);
        assert Objects.equals(result, "hello there") : result;
    }

    @Test
    public void mixed() {
        final Object result = this.run("""
            import [testing, * from testing]
            first + " " + testing
            """);
        assert Objects.equals(result, "hello blob") : result;
    }

}