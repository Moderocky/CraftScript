package mx.kenzie.craftscript.statement;

import mx.kenzie.centurion.ColorProfile;
import mx.kenzie.craftscript.parser.LocalSyntaxParser;
import mx.kenzie.craftscript.script.Context;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.variable.VariableContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.PrintStream;
import java.util.Map;

public record LocalSyntaxStatement(String pattern, Statement<?> function,
                                   Map<String, Statement<?>> data) implements Statement<Object> {

    public static Component prettyPrint(LocalSyntaxParser.Element[] elements, ColorProfile profile) {
        final TextComponent.Builder builder = Component.text();
        boolean first = true;
        for (final LocalSyntaxParser.Element element : elements) {
            if (first) first = false;
            else builder.append(Component.space());
            if (element instanceof LocalSyntaxParser.Literal literal)
                builder.append(Component.text(literal.value(), profile.dark()));
            else if (element instanceof LocalSyntaxParser.Input input) builder
                .append(Component.text('%', profile.pop()))
                .append(Component.text(input.value(), profile.light()))
                .append(Component.text('%', profile.pop()));
        }
        return builder.build();
    }

    @Override
    public Object execute(Context context) throws ScriptError {
        final VariableContainer container = new VariableContainer();
        for (final Map.Entry<String, Statement<?>> entry : data.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue().execute(context);
            container.put(key, value);
            container.getParameters().add(value);
        }
        return RunStatement.execute(context, function, container);
    }

    public static Object execute(Context context) {
        final VariableContainer container = new VariableContainer();
        for (final Map.Entry<String, Statement<?>> entry : data.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue().execute(context);
            container.put(key, value);
            container.getParameters().add(value);
        }
        return RunStatement.execute(context, function, container);
    }

    @Override
    public void debug(PrintStream stream) {
        stream.print(this.getClass().getSimpleName());
        stream.print('[');
        stream.print("pattern=\"");
        stream.print(pattern);
        stream.print("\", function=");
        this.function.debug(stream);
        if (!data.isEmpty()) {
            stream.print(", ");
            stream.print("data=");
            stream.print('[');
            boolean first = true;
            for (final Map.Entry<String, Statement<?>> entry : data.entrySet()) {
                if (first) first = false;
                else stream.print(",");
                stream.print(entry.getKey());
                stream.print('=');
                entry.getValue().debug(stream);
            }
            stream.print(']');
        }
        stream.print(']');
    }

    @Override
    public void stringify(PrintStream stream) {
        String text = pattern;
        for (final Map.Entry<String, Statement<?>> entry : data.entrySet()) {
            text = pattern.replace('%' + entry.getKey() + '%', entry.getValue().stringify());
        }
        stream.print(text);
    }

    @Override
    public Component prettyPrint(ColorProfile profile) {
        if (pattern.indexOf('%') == -1)
            return Component.text(pattern, profile.dark())
                .hoverEvent(Component.textOfChildren(Component.text("Local custom syntax.", profile.light()),
                    Component.newline(),
                    this.printReturnType(profile)));
        final LocalSyntaxParser.Element[] elements = LocalSyntaxParser.tokenize(pattern);
        assert elements.length > 0;
        final TextComponent.Builder builder = Component.text();
        boolean first = true;
        for (final LocalSyntaxParser.Element element : elements) {
            if (first) first = false;
            else builder.append(Component.space());
            if (element instanceof LocalSyntaxParser.Literal literal)
                builder.append(Component.text(literal.value(), profile.dark()));
            else if (element instanceof LocalSyntaxParser.Input input)
                builder.append(data.get(input.value()).prettyPrint(profile));
        }
        return builder.build()
            .hoverEvent(Component.textOfChildren(Component.text("Local custom syntax.", profile.light()),
                Component.newline(),
                prettyPrint(elements, profile),
                Component.newline(),
                this.printReturnType(profile)));
    }

    @Override
    public Class<?> returnType() {
        return function.returnType();
    }

}
