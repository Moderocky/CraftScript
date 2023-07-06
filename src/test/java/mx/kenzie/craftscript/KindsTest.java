package mx.kenzie.craftscript;

import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.kind.Kinds;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KindsTest {

    private static final Pattern
        GETTER_SWITCH = Pattern.compile("getProperty\\(.+\\) \\{[^}]+switch .+ \\{([\\w\\W]+?)default -> (.+)"),
        CASE = Pattern.compile("case \"(\\w+)\" ->");

    @Test
    public void properties() throws Exception {
        for (Kind<?> kind : Kinds.kinds) {
            final Class<?> type = kind.getClass();
            final String name = "src/main/java/" + type.getName().replace('.', '/') + ".java";
            assert !name.contains("$");
            final File file = new File(name);
            assert file.exists() && file.isFile() : "Missing file " + name;
            assert this.checkProperties(kind, file);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void allKindsListed() throws Exception {
        final File folder = new File("src/main/java/mx/kenzie/craftscript/kind/");
        assert folder.exists() && folder.isDirectory() : "Kinds directory is missing.";
        assert folder.isDirectory();
        final File top = new File(folder, "Kind.java");
        assert top.exists() && top.isFile() : "Kind super type missing.";
        final File[] files = folder.listFiles();
        assert files != null && files.length > 0;
        final String path = Kind.class.getPackageName();
        final List<Class<Kind<?>>> kinds = new ArrayList<>(), missing, extra;
        for (final File file : files) {
            if (!file.isFile()) continue;
            if (file.equals(top)) continue;
            final String name = file.getName();
            if (!name.endsWith("Kind.java")) continue;
            final Class<?> type = Class.forName(path + "." + name.substring(0, name.length() - 5));
            assert Kind.class.isAssignableFrom(type) : "Class " + type.getSimpleName() + " does not extend Kind.";
            kinds.add((Class<Kind<?>>) type);
        }
        final Map<Class<Kind<?>>, Field> fields = new LinkedHashMap<>();
        for (final Field field : Kinds.class.getDeclaredFields()) {
            if (field.getType().isArray()) continue;
            if (field.isSynthetic()) continue;
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!Modifier.isPublic(field.getModifiers())) continue;
            if (!Kind.class.isAssignableFrom(field.getType())) continue;
            fields.put((Class<Kind<?>>) field.getType(), field);
        }
        missing = new ArrayList<>(kinds);
        extra = new ArrayList<>(fields.keySet());
        missing.removeAll(extra);
        extra.removeAll(kinds);
        assert fields.keySet().containsAll(kinds) : "Kind list missing: " + missing;
        assert kinds.containsAll(fields.keySet()) : "Kind list has extra: " + extra;
        for (final Kind<?> kind : Kinds.kinds) {
            assert fields.containsKey(kind.getClass()) : "Kind " + kind + " missing from field list.";
            assert kinds.contains(kind.getClass()) : "Kind " + kind + " missing from class list.";
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
