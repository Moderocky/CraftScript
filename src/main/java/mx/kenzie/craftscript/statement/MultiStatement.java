package mx.kenzie.craftscript.statement;

public interface MultiStatement<Result> extends Statement<Result> {

    Statement<?>[] statements();

}
