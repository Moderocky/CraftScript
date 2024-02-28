package mx.kenzie.craftscript.script.core;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.Bridge;

public class CheckedFunction<Lambda extends CheckedFunction.PrettyLambda> implements NativeStatement<Object> {

    protected Kind<?>[] kinds;
    protected Class<?>[] types;
    protected Object[] values;
    protected boolean notNull;
    protected Lambda lambda;

    protected CheckedFunction() {

    }

    public static CheckedFunction<NullaryLambda> nullary() {
        return new CheckedFunction<>();
    }

    public static CheckedFunction<NullaryNoContextLambda> of() {
        return nullary().confirm();
    }

    public static CheckedFunction<NullaryNoContextVoidLambda> ofVoid() {
        return nullary().confirm();
    }

    public static <Arg1> CheckedFunction<UnaryLambda<Arg1>> unary() {
        return new CheckedFunction<>();
    }

    public static <Arg1, Arg2> CheckedFunction<BinaryLambda<Arg1, Arg2>> binary() {
        return new CheckedFunction<>();
    }

    public static <Arg1, Arg2, Arg3> CheckedFunction<TernaryLambda<Arg1, Arg2, Arg3>> ternary() {
        return new CheckedFunction<>();
    }

    public static <Arg1, Arg2, Arg3, Arg4> CheckedFunction<QuaternaryLambda<Arg1, Arg2, Arg3, Arg4>> quaternary() {
        return new CheckedFunction<>();
    }

    public static <Arg1> CheckedFunction<NoContextUnaryLambda<Arg1>> of(Kind<Arg1> arg1) {
        return unary().requires(arg1).confirm();
    }

    public static <Arg1> CheckedFunction<NoContextVoidUnaryLambda<Arg1>> ofVoid(Kind<Arg1> arg1) {
        return unary().requires(arg1).confirm();
    }

    public static <Arg1> CheckedFunction<UnaryLambda<Arg1>> contextualised(Kind<Arg1> arg1) {
        return unary().requires(arg1).confirm();
    }

    public static <Arg1> CheckedFunction<UnaryLambda<Arg1>> contextualised(Class<Arg1> arg1) {
        return unary().requires(arg1).confirm();
    }

    public static <Arg1, Arg2> CheckedFunction<BinaryLambda<Arg1, Arg2>> contextualised(Kind<Arg1> arg1, Kind<Arg2> arg2) {
        return binary().requires(arg1, arg2).confirm();
    }

    public static <Arg1, Arg2> CheckedFunction<NoContextBinaryLambda<Arg1, Arg2>> of(Kind<Arg1> arg1, Kind<Arg2> arg2) {
        return binary().requires(arg1, arg2).confirm();
    }

    public static <Arg1, Arg2> CheckedFunction<NoContextVoidBinaryLambda<Arg1, Arg2>> ofVoid(Kind<Arg1> arg1, Kind<Arg2> arg2) {
        return binary().requires(arg1, arg2).confirm();
    }

    public static <Arg1, Arg2> CheckedFunction<BinaryLambda<Arg1, Arg2>> contextualised(Class<Arg1> arg1, Class<Arg2> arg2) {
        return binary().requires(arg1, arg2).confirm();
    }

    public static <Arg1, Arg2, Arg3> CheckedFunction<TernaryLambda<Arg1, Arg2, Arg3>> contextualised(Kind<Arg1> arg1, Kind<Arg2> arg2, Kind<Arg3> arg3) {
        return ternary().requires(arg1, arg2, arg3).confirm();
    }

    public static <Arg1, Arg2, Arg3> CheckedFunction<TernaryLambda<Arg1, Arg2, Arg3>> contextualised(Class<Arg1> arg1, Class<Arg2> arg2, Class<Arg3> arg3) {
        return ternary().requires(arg1, arg2, arg3).confirm();
    }

    public CheckedFunction<Lambda> runs(Lambda lambda) {
        this.lambda = lambda;
        return this;
    }

    public CheckedFunction<Lambda> requires(Kind<?>... kinds) {
        this.kinds = kinds;
        return this;
    }

    public CheckedFunction<Lambda> requires(Class<?>... types) {
        this.types = types;
        return this;
    }

    public CheckedFunction<Lambda> defaultValues(Object... values) {
        this.values = values;
        return this;
    }

    public CheckedFunction<Lambda> notNull() {
        this.notNull = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <Result extends PrettyLambda> CheckedFunction<Result> confirm() {
        return (CheckedFunction<Result>) this;
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        Arguments arguments = InternalStatement.extractArguments(context);
        if (values != null) arguments = Bridge.defaultValues(arguments, values);
        if (notNull) Bridge.notNull(arguments);
        if (kinds != null) arguments = Bridge.validate(arguments, kinds);
        if (types != null) Bridge.validate(arguments, types);
        if (lambda == null) throw new ScriptError("This function has no behaviour.");
        try {
            return lambda.run(context, arguments);
        } catch (ClassCastException ex) {
            throw new ScriptError("Incorrect arguments were provided.", ex);
        }
    }

    @FunctionalInterface
    public interface QuaternaryLambda<Arg1, Arg2, Arg3, Arg4> extends PrettyLambda {

        Object execute(Context context, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4);

        @Override
        default Object run(Context context, Arguments arguments) {
            return this.execute(context, arguments.get(0), arguments.get(1), arguments.get(2), arguments.get(3));
        }

    }

    @FunctionalInterface
    public interface TernaryLambda<Arg1, Arg2, Arg3> extends PrettyLambda {

        Object execute(Context context, Arg1 arg1, Arg2 arg2, Arg3 arg3);

        @Override
        default Object run(Context context, Arguments arguments) {
            return this.execute(context, arguments.get(0), arguments.get(1), arguments.get(2));
        }

    }

    @FunctionalInterface
    public interface BinaryLambda<Arg1, Arg2> extends PrettyLambda {

        Object execute(Context context, Arg1 arg1, Arg2 arg2);

        @Override
        default Object run(Context context, Arguments arguments) {
            return this.execute(context, arguments.get(0), arguments.get(1));
        }

    }

    @FunctionalInterface
    public interface NoContextBinaryLambda<Arg1, Arg2> extends BinaryLambda<Arg1, Arg2> {

        Object execute(Arg1 arg1, Arg2 arg2);

        @Override
        default Object execute(Context context, Arg1 arg1, Arg2 arg2) {
            return this.execute(arg1, arg2);
        }

    }

    @FunctionalInterface
    public interface NoContextVoidBinaryLambda<Arg1, Arg2> extends BinaryLambda<Arg1, Arg2> {

        void execute(Arg1 arg1, Arg2 arg2);

        @Override
        default Object execute(Context context, Arg1 arg1, Arg2 arg2) {
            this.execute(arg1, arg2);
            return null;
        }

    }

    @FunctionalInterface
    public interface UnaryLambda<Arg1> extends PrettyLambda {

        Object execute(Context context, Arg1 arg1);

        @Override
        default Object run(Context context, Arguments arguments) {
            return this.execute(context, arguments.get(0));
        }

    }

    @FunctionalInterface
    public interface NoContextUnaryLambda<Arg1> extends UnaryLambda<Arg1> {

        Object execute(Arg1 arg1);

        @Override
        default Object execute(Context context, Arg1 arg1) {
            return this.execute(arg1);
        }

    }

    @FunctionalInterface
    public interface NoContextVoidUnaryLambda<Arg1> extends UnaryLambda<Arg1> {

        void execute(Arg1 arg1);

        @Override
        default Object execute(Context context, Arg1 arg1) {
            this.execute(arg1);
            return null;
        }

    }

    @FunctionalInterface
    public interface NullaryLambda extends PrettyLambda {

        Object execute(Context context);

        @Override
        default Object run(Context context, Arguments arguments) {
            return this.execute(context);
        }

    }

    @FunctionalInterface
    public interface NullaryNoContextLambda extends PrettyLambda {

        Object execute();

        @Override
        default Object run(Context context, Arguments arguments) {
            return this.execute();
        }

    }

    @FunctionalInterface
    public interface NullaryNoContextVoidLambda extends PrettyLambda {

        void execute();

        @Override
        default Object run(Context context, Arguments arguments) {
            this.execute();
            return null;
        }

    }

    @FunctionalInterface
    public interface PrettyLambda {

        Object run(Context context, Arguments arguments);

    }

}
