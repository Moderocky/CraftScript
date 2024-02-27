package mx.kenzie.craftscript.environment;

public interface Resource extends AutoCloseable {

    @Override
    void close() throws Exception;

    default void closeSafely() {
        try {
            this.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
