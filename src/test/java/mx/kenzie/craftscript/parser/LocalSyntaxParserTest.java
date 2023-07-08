package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.LocalScriptParser;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.script.ScriptLoader;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LocalSyntaxParserTest {

    @Test
    public void basic() {
        assert this.test("hello %player%", "hello", "player");
        assert this.test("%hello% test %there%", "hello", "test", "there");
    }

    @Test(expected = ScriptError.class)
    public void noClosing() {
        assert this.test("hello %player", "hello", "player");
    }

    @Test(expected = ScriptError.class)
    public void noOpening() {
        assert this.test("hello player%");
    }

    @Test(expected = ScriptError.class)
    public void empty() {
        assert this.test("");
    }

    @Test(expected = ScriptError.class)
    public void blank() {
        assert this.test("    ");
    }

    @Test(expected = ScriptError.class)
    public void twoInputs() {
        assert this.test("%hello% %there%");
    }

    @Test(expected = ScriptError.class)
    public void duplicateInputs() {
        assert this.test("%hello% test %hello%");
    }

    @Test
    public void parseSimple() {
        assert this.match("hello %player%", "hello test");
        assert !this.match("hello %player%", "helloo test");
        assert !this.match("hello %player%", "there test");
        assert this.match("hello %player%", "hello 1 + 1");
    }

    @Test
    public void parseStartEndInputs() {
        assert this.match("%test% hello %player%", "test hello test");
        assert this.match("%test% hello %player%", "1 + 1 hello 2 + 2");
        assert !this.match("%test% hello %player%", "1 + 1 there 2 + 2");
        assert !this.match("%test% hello %player%", "1 + 1 there hello");
        assert !this.match("%test% hello %player%", "1 + 1 hello there hello");
    }

    private boolean match(String pattern, String input) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
            new ByteArrayInputStream(pattern.getBytes(StandardCharsets.UTF_8))));
        final LocalScriptParser parser = new LocalScriptParser(ScriptLoader.BASIC, reader);
        final LocalSyntaxParser local = new LocalSyntaxParser(pattern);
        parser.register(() -> new LocalSyntaxParser(pattern));
        assert local.canUse();
        try (local) {
            local.insert(input, parser);
            if (!local.matches()) return false;
            assert local.parse() != null;
        }
        assert local.canUse();
        return true;
    }

    private boolean test(String pattern) {
        final LocalSyntaxParser parser = new LocalSyntaxParser(pattern);
        return parser.elements.length > 0;
    }

    private boolean test(String pattern, String... strings) {
        final LocalSyntaxParser parser = new LocalSyntaxParser(pattern);
        if (strings.length != parser.elements.length) return false;
        for (int i = 0; i < strings.length; i++) {
            assert strings[i].equals(parser.elements[i].value());
        }
        return true;
    }

}
