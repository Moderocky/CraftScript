package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import net.kyori.adventure.text.Component;

import java.io.PrintStream;

public record ChoiceBlockStatement(BlockStatement either,
                                   BlockStatement or) implements Statement<Object>, EvaluatedStatement<Object> {

    @Override
    public Object execute(Context context) throws ScriptError {
        return this.either(context);
    }

    public Object either(Context context) throws ScriptError {
        return either.execute(context);
    }

    public Object or(Context context) throws ScriptError {
        return or.execute(context);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print("[either=");
        this.either.debug(stream);
        stream.print("[or=");
        this.or.debug(stream);
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        this.either.stringify(stream);
        stream.print(" else ");
        this.or.stringify(stream);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        return Component.textOfChildren(
            this.either.prettyPrint(profile),
            Component.text(" else ", profile.dark())
                .hoverEvent(Component.text("Otherwise do...", profile.light())),
            this.or.prettyPrint(profile)
        );
    }

    @Override
    public Class<?> returnType() {
        if (either.statements().length == 0 && or.statements().length == 0) return Void.class;
        if (either.statements().length == 0) return or.returnType();
        if (or.statements().length == 0) return either.returnType();
        return BlockStatement.getCommonGround(either.returnType(), or.returnType());
    }

    @Override
    public Class<?> evaluatedReturnType() {
        if (either.statements().length == 0 && or.statements().length == 0) return Void.class;
        if (either.statements().length == 0) return or.evaluatedReturnType();
        if (or.statements().length == 0) return either.evaluatedReturnType();
        return BlockStatement.getCommonGround(either.evaluatedReturnType(), or.evaluatedReturnType());
    }

}
