package mx.kenzie.craftscript;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.kind.Kinds;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KindPropertyTest {

    private static final Pattern
        GETTER_SWITCH = Pattern.compile("getProperty\\(.+\\) \\{[^}]+switch .+ \\{([\\w\\W]+?)default -> (.+)"),
        CASE = Pattern.compile("case \"(\\w+)\" ->");

    @Test
    public void test() throws Exception {
        for (Kind<?> kind : Kinds.kinds) {
            final Class<?> type = kind.getClass();
            final String name = "src/main/java/" + type.getName().replace('.', '/') + ".java";
            assert !name.contains("$");
            final File file = new File(name);
            assert file.exists() && file.isFile() : "Missing file " + name;
            assert this.checkProperties(kind, file);
        }
    }

    private boolean checkProperties(Kind<?> kind, File file) throws IOException {
        final String content;
        try (InputStream stream = new FileInputStream(file)) {
            content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        assert !content.isEmpty() : file + " was empty";
        if (!content.contains("getProperty") || !content.contains("case")) return true;
        final Matcher area = GETTER_SWITCH.matcher(content);
        assert area.find() : "No properties in " + file;
        final String chunk = area.group(1);
        final String after = area.group(2);
        assert chunk != null && !chunk.isBlank() : "Found chunk was blank in " + file;
        final Matcher matcher = CASE.matcher(chunk);
        final List<String> list = new ArrayList<>();
        while (matcher.find()) list.add(matcher.group(1));
        if (after.startsWith("super")) list.addAll(List.of(kind.superKind().getProperties()));
        final String[] properties = kind.getProperties();
        final List<String> current = new ArrayList<>(List.of(properties));
        if (new HashSet<>(list).equals(new HashSet<>(current))) return true;
        current.removeAll(list);
        final List<String> missing = new ArrayList<>(list);
        missing.removeAll(List.of(properties));
        throw new AssertionError("Incorrect properties for " + file.getName() + "\n"
            + (current.isEmpty() ? "" : "\tHas extra:\t" + current + "\n")
            + (missing.isEmpty() ? "" : "\tMissing:\t" + missing + "\n")
            + (list.isEmpty() ? "" : "\tFull list:\n{\"" + String.join("\", \"", list) + "\"}")
        );
    }

}
