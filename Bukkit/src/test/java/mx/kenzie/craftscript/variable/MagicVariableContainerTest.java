package mx.kenzie.craftscript.variable;

import mx.kenzie.foundation.Loader;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class MagicVariableContainerTest {

    public static void main(String... args) {
        final int revs = 10000000;
        final var type = MagicVariableContainer.compile(MagicVariableContainer.class, Loader.DEFAULT, "one", "two");
        final Map<String, Object> magic = MagicVariableContainer.create(type);
        final Map<String, Object> regular = new LinkedHashMap<>();
        for (int i = 0; i < 100; i++) { // warm up
            regular.put("one", 1);
            regular.put("two", 2);
            if (!regular.get("one").equals(1)) throw new Error();
            if (!regular.get("two").equals(2)) throw new Error();
            magic.put("one", 1);
            magic.put("two", 2);
            if (!magic.get("one").equals(1)) throw new Error();
            if (!magic.get("two").equals(2)) throw new Error();
        }
        {
            final long start = System.currentTimeMillis(), end;
            for (int i = 0; i < revs; i++) {
                regular.put("one", 1);
                regular.put("two", 2);
                regular.get("one");
                regular.get("two");
            }
            end = System.currentTimeMillis();
            System.out.println("Regular hash map took " + (end - start) + "ms for " + (revs * 2) + " write + reads.");
        }
        {
            final long start = System.currentTimeMillis(), end;
            for (int i = 0; i < revs; i++) {
                magic.put("one", 1);
                magic.put("two", 2);
                magic.get("one");
                magic.get("two");
            }
            end = System.currentTimeMillis();
            System.out.println("Magic map took " + (end - start) + "ms for " + (revs * 2) + " write + reads.");
        }
    }

    @Test
    public void simple() {
        final var type = MagicVariableContainer.compile(MagicVariableContainer.class, Loader.DEFAULT, "one", "two");
        assert type != null;
        final MagicVariableContainer container = MagicVariableContainer.create(type);
        container.put("one", 1);
        assert container.get("one").equals(1);
    }

}
