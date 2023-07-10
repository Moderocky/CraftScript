package mx.kenzie.craftscript.statement;

public interface TextStatement extends Statement<String> {

    String value();

    @Override
    default Class<? extends String> returnType() {
        return String.class;
    }

    @Override
    default boolean knowsReturnType() {
        return true;
    }

}
