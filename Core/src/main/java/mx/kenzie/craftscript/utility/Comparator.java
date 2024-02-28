package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.statement.BlockStatement;
import mx.kenzie.craftscript.statement.IfStatement;
import mx.kenzie.craftscript.variable.Wrapper;

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
    }, EQ = new SimpleComparator("==") {
        @Override
        public Object compare(Object a, Object b) {
            if (a == b) return true;
            else if (Objects.equals(a, b)) return true;
            else if (a instanceof Number first && b instanceof Number second)
                return first.doubleValue() == second.doubleValue();
            else return false;
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            return Boolean.class;
        }
    }, NE = new SimpleComparator("!=") {
        @Override
        public Object compare(Object a, Object b) {
            if (a == b) return false;
            else if (Objects.equals(a, b)) return false;
            else if (a instanceof Number first && b instanceof Number second)
                return first.doubleValue() != second.doubleValue();
            else return true;
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


    }, PLUS = new SimpleComparator("+") {
        @Override
        public Object compare(Object a, Object b) {
            if (a instanceof String string) return string + b;
            if (b instanceof String string) return a + string;
            if (!(a instanceof Number first)) return null;
            if (!(b instanceof Number second)) return null;
            if (first instanceof Integer && second instanceof Integer) return first.intValue() + second.intValue();
            return first.doubleValue() + second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            if (antecedent == Integer.class && consequent == Integer.class) return Integer.class;
            if (Number.class.isAssignableFrom(antecedent) && Number.class.isAssignableFrom(consequent))
                return Number.class;
            return String.class;
        }
    }, MINUS = new SimpleComparator("-") {
        @Override
        public Object compare(Object a, Object b) {
            if (!(a instanceof Number first) || !(b instanceof Number second)) return null;
            if (first instanceof Integer && second instanceof Integer) return first.intValue() - second.intValue();
            return first.doubleValue() - second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            if (antecedent == Integer.class && consequent == Integer.class) return Integer.class;
            return Number.class;
        }
    }, TIMES = new SimpleComparator("*") {
        @Override
        public Object compare(Object a, Object b) {
            if (!(a instanceof Number first) || !(b instanceof Number second)) return null;
            if (first instanceof Integer && second instanceof Integer) return first.intValue() * second.intValue();
            return first.doubleValue() * second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            if (antecedent == Integer.class && consequent == Integer.class) return Integer.class;
            return Number.class;
        }
    }, DIVIDE = new SimpleComparator("/") {
        @Override
        public Object compare(Object a, Object b) {
            if (!(a instanceof Number first) || !(b instanceof Number second)) return null;
            if (first instanceof Integer && second instanceof Integer) return first.intValue() / second.intValue();
            return first.doubleValue() / second.doubleValue();
        }

        @Override
        public Class<?> getReturnType(Class<?> antecedent, Class<?> consequent) {
            if (antecedent == Integer.class && consequent == Integer.class) return Integer.class;
            return Number.class;
        }
    };

    Object compare(Object a, Object b);

    default Object compareWrapped(Object a, Object b) {
        return this.compare(Wrapper.unwrap(a), Wrapper.unwrap(b));
    }

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
