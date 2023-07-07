package mx.kenzie.craftscript.statement;

public interface EvaluatedStatement<Result> extends Statement<Result> {

    Class<?> evaluatedReturnType();

    default boolean knowsEvaluatedReturnType() {
        return this.evaluatedReturnType() != Object.class;
    }

}
