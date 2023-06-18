package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.variable.LibraryObject;

import java.util.function.Function;

public class MathLibrary extends LibraryObject {

    private static final InternalStatement
        FLOOR = new InternalStatement((UnaryDouble) Math::floor),
        CEIL = new InternalStatement((UnaryDouble) Math::ceil),
        ROUND = new InternalStatement((UnaryDouble) Math::round),
        SIN = new InternalStatement((UnaryDouble) Math::sin),
        COS = new InternalStatement((UnaryDouble) Math::cos),
        TAN = new InternalStatement((UnaryDouble) Math::tan),
        ASIN = new InternalStatement((UnaryDouble) Math::asin),
        ACOS = new InternalStatement((UnaryDouble) Math::acos),
        ATAN = new InternalStatement((UnaryDouble) Math::atan),
        ATAN2 = new InternalStatement((BinaryDouble) Math::atan2),
        SINH = new InternalStatement((UnaryDouble) Math::sinh),
        COSH = new InternalStatement((UnaryDouble) Math::cosh),
        TANH = new InternalStatement((UnaryDouble) Math::tanh),
        SIGNUM = new InternalStatement((UnaryDouble) Math::signum),
        LOG = new InternalStatement((UnaryDouble) Math::log),
        EXP = new InternalStatement((UnaryDouble) Math::exp),
        POW = new InternalStatement((BinaryDouble) Math::pow),
        HYPOT = new InternalStatement((BinaryDouble) Math::hypot),
        TO_DEGREES = new InternalStatement((UnaryDouble) Math::toDegrees),
        TO_RADIANS = new InternalStatement((UnaryDouble) Math::toRadians),
        SQRT = new InternalStatement((UnaryDouble) Math::sqrt),
        CBRT = new InternalStatement((UnaryDouble) Math::cbrt),
        ABS = new InternalStatement((Unary) value -> {
            if (value instanceof Integer integer) return Math.abs(integer);
            else return Math.abs(value.doubleValue());
        }),
        MAX = new InternalStatement((Binary) (a, b) -> {
            if (a instanceof Integer integer) return Math.max(integer, b.intValue());
            else return Math.max(a.doubleValue(), b.doubleValue());
        }),
        MIN = new InternalStatement((Binary) (a, b) -> {
            if (a instanceof Integer integer) return Math.min(integer, b.intValue());
            else return Math.min(a.doubleValue(), b.doubleValue());
        });

    public MathLibrary() {
        super("floor", "ceil", "round", "sin", "cos", "tan", "asin", "acos", "atan", "atan2", "sinh", "cosh", "tanh",
            "signum", "log", "exp", "pow", "hypot", "to_degrees", "to_radians", "sqrt", "cbrt", "abs", "max", "min");
    }

    @Override
    public Object get(String key) {
        return switch (key) {
            case "floor" -> FLOOR;
            case "ceil" -> CEIL;
            case "round" -> ROUND;
            case "sin" -> SIN;
            case "cos" -> COS;
            case "tan" -> TAN;
            case "asin" -> ASIN;
            case "acos" -> ACOS;
            case "atan" -> ATAN;
            case "atan2" -> ATAN2;
            case "sinh" -> SINH;
            case "cosh" -> COSH;
            case "tanh" -> TANH;
            case "signum" -> SIGNUM;
            case "log" -> LOG;
            case "exp" -> EXP;
            case "pow" -> POW;
            case "hypot" -> HYPOT;
            case "to_degrees" -> TO_DEGREES;
            case "to_radians" -> TO_RADIANS;
            case "sqrt" -> SQRT;
            case "cbrt" -> CBRT;
            case "abs" -> ABS;
            case "max" -> MAX;
            case "min" -> MIN;
            default -> null;
        };
    }

    @FunctionalInterface
    interface Binary extends Function<Arguments, Object> {

        Number run(Number first, Number second);

        @Override
        default Object apply(Arguments arguments) {
            return this.run(arguments.get(0), arguments.get(1));
        }

    }

    @FunctionalInterface
    interface Unary extends Function<Arguments, Object> {

        Number run(Number number);

        @Override
        default Object apply(Arguments arguments) {
            return this.run(arguments.get(0));
        }

    }

    @FunctionalInterface
    interface UnaryDouble extends Function<Arguments, Object> {

        Number run(double number);

        @Override
        default Object apply(Arguments arguments) {
            return this.run(arguments.<Number>get(0).doubleValue());
        }

    }

    @FunctionalInterface
    interface BinaryDouble extends Function<Arguments, Object> {

        Number run(double first, double second);

        @Override
        default Object apply(Arguments arguments) {
            return this.run(arguments.<Number>get(0).doubleValue(), arguments.<Number>get(1).doubleValue());
        }

    }

}
