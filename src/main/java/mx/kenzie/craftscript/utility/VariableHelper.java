package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.statement.Statement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface VariableHelper {

    ThreadLocal<VariableHelper> local = new ThreadLocal<>();
    VariableHelper EMPTY = new VariableHelper() {
        @Override
        public Statement<?> getAssignment(String name) {
            return null;
        }

        @Override
        public Class<?> getReturnType(String name) {
            return Object.class;
        }

        @Override
        public VariableHelper clone() {
            return this;
        }
    };

    static VariableHelper instance() {
        final VariableHelper helper = local.get();
        if (helper == null) return EMPTY;
        return helper;
    }

    static void init() {
        local.set(new SimpleVariableHelper());
    }

    static void tearDown() {
        local.remove();
    }

    @Nullable Statement<?> getAssignment(String name);

    @NotNull Class<?> getReturnType(String name);

    default void assign(String name, Statement<?> statement) {}

    default void assign(String name, Class<?> type) {}

    default void assignIfAbsent(String name, Class<?> type) {}

    default VariableHelper clone() {
        return this;
    }

    default void purge() {
    }

    default boolean isKnown(String name) {
        return false;
    }

}

class SimpleVariableHelper implements VariableHelper {

    protected final Map<String, Statement<?>> map = new HashMap<>();
    protected final Map<String, Class<?>> types = new HashMap<>();

    @Override
    public Statement<?> getAssignment(String name) {
        return map.get(name);
    }

    @Override
    public boolean isKnown(String name) {
        return types.containsKey(name);
    }

    @Override
    public Class<?> getReturnType(String name) {
        return types.getOrDefault(name, Object.class);
    }

    @Override
    public void assign(String name, Statement<?> statement) {
        this.map.put(name, statement);
        this.assign(name, statement.returnType());
    }

    @Override
    public void assign(String name, Class<?> type) {
        this.types.put(name, type);
    }

    @Override
    public void assignIfAbsent(String name, Class<?> type) {
        this.types.putIfAbsent(name, type);
    }

    @Override
    public VariableHelper clone() {
        final SimpleVariableHelper helper = new SimpleVariableHelper();
        helper.map.putAll(this.map);
        return helper;
    }

    @Override
    public void purge() {
        this.map.clear();
        this.types.clear();
    }

}
