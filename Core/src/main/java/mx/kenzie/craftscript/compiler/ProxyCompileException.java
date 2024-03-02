package mx.kenzie.craftscript.compiler;

public class ProxyCompileException extends Exception {

    public ProxyCompileException() {
        super();
    }

    public ProxyCompileException(String message) {
        super(message);
    }

    public ProxyCompileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyCompileException(Throwable cause) {
        super(cause);
    }

    protected ProxyCompileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
