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
        public void assign(String name, Statement<?> statement) {
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

    void assign(String name, Statement<?> statement);

}

class SimpleVariableHelper implements VariableHelper {

    protected final Map<String, Statement<?>> map = new HashMap<>();

    @Override
    public Statement<?> getAssignment(String name) {
        return map.get(name);
    }

    @Override
    public Class<?> getReturnType(String name) {
        final Statement<?> statement = this.getAssignment(name);
        if (statement == null) return Object.class;
        return statement.returnType();
    }

    @Override
    public void assign(String name, Statement<?> statement) {
        map.put(name, statement);
    }

}
