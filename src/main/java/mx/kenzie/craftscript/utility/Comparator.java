package mx.kenzie.craftscript.utility;

import mx.kenzie.craftscript.statement.IfStatement;

import java.util.Objects;

public interface Comparator {

    Comparator LESS_THAN = new SimpleComparator("<") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() < second.doubleValue();
        }
    }, LESS_EQUAL = new SimpleComparator("<=") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() <= second.doubleValue();
        }
    }, GREATER_THAN = new SimpleComparator(">") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() > second.doubleValue();
        }
    }, GREATER_EQUAL = new SimpleComparator(">=") {
        @Override
        public Boolean compare(Object a, Object b) {
            return a instanceof Number first && b instanceof Number second && first.doubleValue() >= second.doubleValue();
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
    }, OR = new SimpleComparator("|") {
        @Override
        public Object compare(Object a, Object b) {
            if (a instanceof Boolean first && b instanceof Boolean second) return first || second;
            if (a instanceof Integer first && b instanceof Integer second) return first | second;
            if (a instanceof Long first && b instanceof Long second) return first | second;
            if (a instanceof Byte first && b instanceof Byte second) return first | second;
            return IfStatement.value(a) || IfStatement.value(b);
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
    }, NE = new SimpleComparator("!=") {
        @Override
        public Object compare(Object a, Object b) {
            return !Objects.equals(a, b);
        }
    }, ALT = new SimpleComparator("?") {
        @Override
        public Object compare(Object a, Object b) {
            return a != null ? a : b;
        }
    };

    Object compare(Object a, Object b);

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
