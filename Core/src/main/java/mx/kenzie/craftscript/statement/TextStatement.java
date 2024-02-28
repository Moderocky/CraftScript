package mx.kenzie.craftscript.statement;

public interface TextStatement extends Statement<String> {

    String value();

    @Override
    default boolean knowsReturnType() {
        return true;
    }

    @Override
    default Class<? extends String> returnType() {
        return String.class;
    }

}
