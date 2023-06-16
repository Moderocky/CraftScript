package mx.kenzie.craftscript;

public interface Executable<Result> extends Runnable {

    Result execute(Context context) throws ScriptError;

    @Override
    default void run() {
        final Context context = Context.getLocalContext();
        if (context == null) throw new ScriptError("No context is available to run this script.");
        this.execute(context);
    }

}
