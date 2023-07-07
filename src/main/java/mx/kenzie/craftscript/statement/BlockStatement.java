package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.utility.PrettyPrinter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;

public record BlockStatement(Statement<?>... statements) implements Statement<Object>, EvaluatedStatement<Object> {

    static void prettyPrint(ColorProfile profile, TextComponent.Builder text, Statement<?>... block) {
        text.append(Component.text('{', profile.pop()));
        if (block.length < 1) text.append(Component.space());
        else {
            PrettyPrinter.incrementIndent();
            text.append(Component.newline());
            for (final Statement<?> statement : block) {
                text.append(statement.prettyPrint(profile));
                text.append(Component.newline());
            }
            PrettyPrinter.decrementIndent();
            text.append(Component.text(PrettyPrinter.getIndent(), profile.light()));
        }
        text.append(Component.text('}', profile.pop()));

    }

    public static Class<?> getCommonGround(Class<?> first, Class<?> second) {
        if (first.equals(second)) return first;
        if (first.isAssignableFrom(second)) return first;
        if (second.isAssignableFrom(first)) return second;
        if (first == Void.class) return second;
        if (second == Void.class) return first;
        Class<?> test = first;
        do {
            if (test.isAssignableFrom(second)) return test;
            test = test.getSuperclass();
        } while (test != Object.class);
        test = second;
        do {
            if (test.isAssignableFrom(first)) return test;
            test = test.getSuperclass();
        } while (test != Object.class);
        for (final Class<?> face : first.getInterfaces()) {
            if (face.isAssignableFrom(second)) return face;
        }
        return Object.class;
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        Object result = null;
        for (final Statement<?> statement : statements) result = statement.execute(context);
        return result;
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.println('[');
        for (final Statement<?> statement : statements) {
            stream.print('\t');
            statement.debug(stream);
            stream.println();
        }
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        stream.print('{');
        if (statements.length < 1) stream.print(' ');
        else {
            PrettyPrinter.incrementIndent();
            stream.println();
            for (final Statement<?> statement : statements) {
                statement.stringify(stream);
                stream.println();
            }
            PrettyPrinter.decrementIndent();
            stream.print(PrettyPrinter.getIndent());
        }
        stream.print('}');
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        final TextComponent.Builder text = Component.text();
        BlockStatement.prettyPrint(profile, text, statements);
        return text.build();
    }

    @Override
    public Class<?> returnType() {
        if (statements.length == 0) return Void.class;
        return statements[statements.length - 1].returnType();
    }

    @Override
    public Class<?> evaluatedReturnType() {
        if (statements.length == 0) return Void.class;
        final Statement<?> statement = statements[statements.length - 1];
        return statement instanceof EvaluatedStatement<?> evaluated ? evaluated.evaluatedReturnType() : Void.class;
    }

}
