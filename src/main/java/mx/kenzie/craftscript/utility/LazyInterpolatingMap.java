package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.Statement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LazyInterpolatingMap extends AbstractMap<String, Object> {

    private final Map<String, Statement<?>> map = new LinkedHashMap<>();
    private final Context context;
    private final Set<Entry<String, Object>> entrySet = new LinkedHashSet<>();

    public LazyInterpolatingMap(Context context, InterpolationStatement... interpolations) {
        this.context = context;
        for (final InterpolationStatement interpolation : interpolations) {
            this.map.put(interpolation.key(), interpolation.statement());
            this.entrySet.add(new LazyEntry(interpolation.key(), interpolation.statement()));
        }
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return entrySet;
    }

    @Override
    public Object get(Object key) {
        return map.get(key).execute(context);
    }

    final class LazyEntry implements Entry<String, Object> {

        private final String getKey;
        private final Statement<?> statement;

        LazyEntry(String getKey, Statement<?> statement) {
            this.getKey = getKey;
            this.statement = statement;
        }

        @Override
        public Object getValue() {
            return statement.execute(context);
        }

        @Override
        public Object setValue(Object value) {
            return null;
        }

        @Override
        public String getKey() {return getKey;}

        public Statement<?> statement() {return statement;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LazyEntry) obj;
            return Objects.equals(this.getKey, that.getKey) &&
                Objects.equals(this.statement, that.statement);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKey, statement);
        }

        @Override
        public String toString() {
            return "LazyEntry[" +
                "getKey=" + getKey + ", " +
                "statement=" + statement + ']';
        }

    }

}
