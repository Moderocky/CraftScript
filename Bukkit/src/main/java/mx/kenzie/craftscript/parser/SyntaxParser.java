package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.statement.SyntaxStatement;
import mx.kenzie.craftscript.statement.TextStatement;
import mx.kenzie.craftscript.utility.VariableHelper;

public class SyntaxParser extends BasicParser {

    protected TextStatement pattern;
    protected Statement<?> function;

    @Override
    public boolean matches() {
        if (!input.startsWith("syntax ")) return false;
        final String text = input.substring("syntax ".length());
        int start = 0;
        do {
            final int equals = text.indexOf('=', start);
            if (equals < 0) return false;
            if (start >= text.length()) return false;
            final String before, after;
            before = text.substring(0, equals).trim();
            if (before.charAt(0) != '"') throw new ScriptError("Line " + parent.getLine()
                + ": Syntax statement expects a literal \"text\" pattern, found '" + before + "'");
            start = equals + 1;
            if (before.charAt(before.length() - 1) != '"') continue;
            if (!(parent.parse(before) instanceof TextStatement statement)) continue;
            this.pattern = statement;
            after = text.substring(start).trim();
            VariableHelper helper = VariableHelper.instance(), child = helper.clone();
            try {
                child.purge(); // outer variables aren't available in syntax
                for (final LocalSyntaxParser.Element element : LocalSyntaxParser.tokenize(pattern.value())) {
                    if (element instanceof LocalSyntaxParser.Input) child.assign(element.value(), Object.class);
                }
                VariableHelper.local.set(child);
                this.function = parent.parse(after);
            } finally {
                VariableHelper.local.set(helper);
            }
            if (function == null) continue;
            return true;
        } while (start < text.length());
        return false;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        if (this.pattern.value().isBlank()) throw new ScriptError("The syntax pattern must not be blank.");
        final TextStatement pattern = this.pattern;
        final Statement<?> function = this.function;
        this.parent.register(() -> new LocalSyntaxParser(pattern.value(), function));
        return new SyntaxStatement(pattern, function);
    }

    @Override
    public void close() throws ScriptError {
        this.pattern = null;
        this.function = null;
        super.close();
    }

}
