package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.IfStatement;

import java.util.Objects;

public interface Comparator {

    Comparator LESS_THAN = new SimpleComparator("<") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() < second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            return Boolean.class;
        }
    }, LESS_EQUAL = new SimpleComparator("<=") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() <= second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            return Boolean.class;
        }
    }, GREATER_THAN = new SimpleComparator(">") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() > second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            return Boolean.class;
        }
    }, GREATER_EQUAL = new SimpleComparator(">=") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() >= second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            return Boolean.class;
        }
    }, AND = new SimpleComparator("&") {
        @Override
        public Object compare(Object a, Object b) {
            if (a instanceof Boolean first && b instanceof Boolean second) return first && second;
            if (a instanceof Integer first && b instanceof Integer second) return first & second;
            if (a instanceof Long first && b instanceof Long second) return first & second;
            if (a instanceof Byte first && b instanceof Byte second) return first & second;
            return IfStatement.value(a) && IfStatement.value(b);
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            if (Integer.class.isAssignableFrom(antecedent) && Integer.class.isAssignableFrom(consequent))
                return Integer.class;
            if (Number.class.isAssignableFrom(antecedent) && Number.class.isAssignableFrom(consequent))
                return Number.class;
            return Boolean.class;
        }
    }, OR = new SimpleComparator("|") {
        @Override
        public Object compare(Object a, Object b) {
            if (a instanceof Boolean first && b instanceof Boolean second) return first || second;
            if (a instanceof Integer first && b instanceof Integer second) return first | second;
            if (a instanceof Long first && b instanceof Long second) return first | second;
            if (a instanceof Byte first && b instanceof Byte second) return first | second;
            return IfStatement.value(a) || IfStatement.value(b);
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            if (Integer.class.isAssignableFrom(antecedent) && Integer.class.isAssignableFrom(consequent))
                return Integer.class;
            if (Number.class.isAssignableFrom(antecedent) && Number.class.isAssignableFrom(consequent))
                return Number.class;
            return Boolean.class;
        }
    }, XOR = new SimpleComparator("^") {
        @Override
        public Object compare(Object a, Object b) {
            if (a instanceof Boolean first && b instanceof Boolean second) return first ^ second;
            if (a instanceof Integer first && b instanceof Integer second) return first ^ second;
            if (a instanceof Long first && b instanceof Long second) return first ^ second;
            if (a instanceof Byte first && b instanceof Byte second) return first ^ second;
            return IfStatement.value(a) ^ IfStatement.value(b);
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            if (Integer.class.isAssignableFrom(antecedent) && Integer.class.isAssignableFrom(consequent))
                return Integer.class;
            if (Number.class.isAssignableFrom(antecedent) && Number.class.isAssignableFrom(consequent))
                return Number.class;
            return Boolean.class;
        }
    }, NE = new SimpleComparator("!=") {
        @Override
        public Object compare(Object a, Object b) {
            return !Objects.equals(a, b);
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            return Boolean.class;
        }
    }, ALT = new SimpleComparator("?") {
        @Override
        public Object compare(Object a, Object b) {
            return a != null ? a : b;
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            return BlockStatement.getCommonGround(antecedent, consequent);
        }


    };

    Object compare(Object a, Object b);

    Class<?> getReturnType(Class<?> antecedent, Class<?> consequent);

}

abstract class SimpleComparator implements Comparator {

    private final String operator;

    SimpleComparator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }

}
