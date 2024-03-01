package mx.kenzie.craftscript.script.core;

import mx.kenzie.craftscript.script.SimpleScriptLoader;
import mx.kenzie.craftscript.script.SystemScriptManager;
import mx.kenzie.craftscript.variable.Wrapper;
import org.junit.Test;

import java.util.Objects;

public class SuperFunctionTest {

    static SystemScriptManager manager = new SystemScriptManager(SimpleScriptLoader.BASIC);

    @Test
    public void simple() {
        final Object result = this.run("""
            blob = fn() {
                "hello"
            }
                        
            blob()
            """);
        assert Objects.equals(result, "hello") : result;
    }

    @Test
    public void noArgs() {
        final Object result = this.run("""
            blob = fn(text) {
                "hello"
            }
                        
            blob()
            """);
        assert Objects.equals(result, "hello") : result;
    }

    @Test
    public void noParams() {
        final Object result = this.run("""
            blob = fn() {
                "hello"
            }
                        
            blob("hello")
            """);
        assert Objects.equals(result, "hello") : result;
    }

    @Test
    public void oneArg() {
        final Object result = this.run("""
            blob = fn(text) {
                text
            }
                        
            blob("there")
            """);
        assert Objects.equals(result, "there") : result;
    }

    @Test
    public void defaultParameterNoInput() {
        final Object result = this.run("""
            blob = fn(text = "hello") {
                text
            }
                        
            blob()
            """);
        assert Objects.equals(result, "hello") : result;
    }

    @Test
    public void defaultParameterInput() {
        final Object result = this.run("""
            blob = fn(text = "hello") {
                text
            }
                        
            blob("there")
            """);
        assert Objects.equals(result, "there") : result;
    }

    @Test
    public void defaultParameterStatement() {
        final Object result = this.run("""
            blob = fn(text = 1 + 5) {
                text
            }
                        
            blob()
            """);
        assert Objects.equals(result, 6) : result;
    }

    @Test
    public void defaultParameterStatementInput() {
        final Object result = this.run("""
            blob = fn(text = 1 + 5) {
                text
            }
                        
            blob("test")
            """);
        assert Objects.equals(result, "test") : result;
    }

    private Object run(String source) {
        return Wrapper.unwrap(manager.runScript(manager.parseScript(source), System.out));
    }

    private Object debug(String source) {
        manager.parseScript(source).debug(System.out);
        return Wrapper.unwrap(manager.runScript(manager.parseScript(source), System.out));
    }

}