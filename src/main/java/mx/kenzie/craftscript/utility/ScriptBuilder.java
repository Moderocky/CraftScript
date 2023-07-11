package mx.kenzie.craftscript.utility;

import mx.kenzie.centurion.selector.Universe;
import mx.kenzie.craftscript.parser.LocalSyntaxParser;
import mx.kenzie.craftscript.script.AbstractScript;
import mx.kenzie.craftscript.script.AnonymousScript;
import mx.kenzie.craftscript.script.Script;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface ScriptBuilder {

    ThreadLocal<AtomicInteger> lineCounter = new ThreadLocal<>();
    ThreadLocal<Map<String, SyntaxStatement>> localSyntax = new ThreadLocal<>();

    private static Map<String, SyntaxStatement> getLocalSyntax() {
        Map<String, SyntaxStatement> map = localSyntax.get();
        if (map == null) localSyntax.set(map = new HashMap<>());
        return map;
    }

    private static int getLineNumber() {
        return getCounter().get();
    }

    private static AtomicInteger getCounter() {
        AtomicInteger integer = lineCounter.get();
        if (integer == null) lineCounter.set(integer = new AtomicInteger(1));
        return integer;
    }

    private static int incrementLine() {
        return getCounter().getAndIncrement();
    }

    private static VariableAssignmentStatement[] assign(Map<String, Statement<?>> entries) {
        final VariableAssignmentStatement[] statements = new VariableAssignmentStatement[entries.size()];
        final Iterator<Map.Entry<String, Statement<?>>> iterator = entries.entrySet().iterator();
        for (int i = 0; i < statements.length; i++) {
            final Map.Entry<String, Statement<?>> entry = iterator.next();
            statements[i] = set(var(entry.getKey()), entry.getValue());
        }
        return statements;
    }

    private static LineStatement[] lines(Statement<?>... statements) {
        final LineStatement[] lines = new LineStatement[statements.length];
        for (int i = 0; i < lines.length; i++) {
            if (statements[i] instanceof LineStatement line) lines[i] = line;
            else lines[i] = line(statements[i]);
        }
        return lines;
    }

    static AbstractScript script(Statement<?>... lines) {
        return script(lines(lines));
    }

    static AbstractScript script(LineStatement... lines) {
        try {
            return new AnonymousScript(lines);
        } finally {
            localSyntax.remove();
            lineCounter.remove();
        }
    }

    static Script script(String name, Statement<?>... lines) {
        return script(name, lines(lines));
    }

    static Script script(String name, LineStatement... lines) {
        try {
            return new Script(name, lines);
        } finally {
            localSyntax.remove();
            lineCounter.remove();
        }
    }

    static LineStatement line(Statement<?> statement) {
        return new LineStatement(statement, incrementLine());
    }

    static BlockStatement block(Statement<?>... statements) {
        return new BlockStatement(lines(statements));
    }

    static BlockStatement block(LineStatement... lines) {
        return new BlockStatement(lines);
    }

    static ChoiceBlockStatement choose(BlockStatement either, BlockStatement or) {
        return new ChoiceBlockStatement(either, or);
    }

    static VariableAssignmentStatement set(VariableStatement variable, Statement<?> value) {
        return new VariableAssignmentStatement(variable.name(), value);
    }

    static SetterStatement set(Property property, Statement<?> value) {
        return new SetterStatement(property.source, property.property, value);
    }

    static GetterStatement get(Property property) {
        return new GetterStatement(property.source, property.property);
    }

    static VariableStatement var(String variable) {
        return new VariableStatement(variable);
    }

    static Property property(Statement<?> source, String property) {
        return new Property(source, property);
    }

    static CommandStatement command(String command, InterpolationStatement... interpolations) {
        return new CommandStatement(command, interpolations);
    }

    static LiteralStringStatement string(String value) {
        return new LiteralStringStatement(value);
    }

    static StringStatement string(String value, InterpolationStatement... interpolations) {
        return new StringStatement(value, interpolations);
    }

    static LiteralStatement literal(Object value) {
        return new LiteralStatement(value);
    }

    static BinaryStatement<?> compare(Statement<?> antecedent, Comparator comparator, Statement<?> consequent) {
        return new CompareStatement(antecedent, consequent, comparator);
    }

    static EqualsStatement is(Statement<?> antecedent, Statement<?> consequent) {
        return new EqualsStatement(antecedent, consequent);
    }

    static InvertStatement not(Statement<?> value) {
        return new InvertStatement(value);
    }

    static InvertStatement not(Boolean value) {
        return not(literal(value));
    }

    static FunctionStatement function(Statement<?> statement) {
        return new FunctionStatement(statement);
    }

    static InterpolationStatement interpolate(Statement<?> statement) {
        return new InterpolationStatement('{' + statement.stringify() + '}', statement);
    }

    static KindStatement kind(String name) {
        return new KindStatement(name);
    }

    static ListStatement list(Statement<?>... statements) {
        return new ListStatement(statements);
    }

    static LocalFunctionStatement localFunction(Statement<?> task) {
        return localFunction(task, null);
    }

    static LocalFunctionStatement localFunction(Statement<?> task, Statement<?> context) {
        return new LocalFunctionStatement(task, context);
    }

    static LocalKeywordStatement localKeyword(String name) {
        return new LocalKeywordStatement(name);
    }

    static LocalSyntaxStatement localSyntax(String pattern, Statement<?>... inputs) {
        final SyntaxStatement statement = getLocalSyntax().get(pattern);
        if (statement == null) throw new ScriptError("Syntax '" + pattern + "' is not known.");
        final LocalSyntaxParser.Element[] elements = LocalSyntaxParser.tokenize(pattern);
        final Iterator<Statement<?>> iterator = List.of(inputs).iterator();
        final Map<String, Statement<?>> map = new LinkedHashMap<>();
        for (final LocalSyntaxParser.Element element : elements) {
            if (element instanceof LocalSyntaxParser.Literal) continue;
            if (!iterator.hasNext()) throw new ScriptError("Not enough inputs provided for '" + pattern + "'.");
            map.put(element.value(), iterator.next());
        }
        return new LocalSyntaxStatement(pattern, statement.function(), map);
    }

    static MapStatement map(Map<String, Statement<?>> entries) {
        return map(assign(entries));
    }

    static MapStatement map(VariableAssignmentStatement... entries) {
        return new MapStatement(entries);
    }

    static ScriptStatement script(String name) {
        return new ScriptStatement(name);
    }

    static SelectorStatement selector(String text, Universe<?> universe, InterpolationStatement... interpolations) {
        return new SelectorStatement(text, universe, interpolations);
    }

    static StructStatement struct_(Map<String, Statement<?>> entries) {
        return struct_(assign(entries));
    }

    static StructStatement struct_(VariableAssignmentStatement... entries) {
        return new StructStatement(block(entries));
    }

    static StructStatement struct_(BlockStatement block) {
        return new StructStatement(block);
    }

    static RequireStatement require_(String... names) {
        return new RequireStatement(names);
    }

    static NullStatement null_() {
        return new NullStatement();
    }

    static SyntaxStatement syntax_(String pattern, Statement<?> task) {
        final Map<String, SyntaxStatement> map = getLocalSyntax();
        final SyntaxStatement statement = new SyntaxStatement(new StringStatement(pattern), task);
        map.put(pattern, statement);
        return statement;
    }

    static ListenerStatement on_(Statement<?> key, Statement<?> task) {
        return on_(key, null, task);
    }

    static ListenerStatement on_(Statement<?> key, Statement<?> details, Statement<?> task) {
        return new ListenerStatement(key, details, task);
    }

    static ImportStatement import_(String... names) {
        return new ImportStatement(names);
    }

    static RunStatement run_(Statement<?> task) {
        return run_(task, null);
    }

    static RunStatement run_(Statement<?> task, Statement<?> context) {
        return new RunStatement(task, context);
    }

    static AssertStatement assert_(Statement<?> statement) {
        return new AssertStatement(statement);
    }

    static DoStatement do_(Statement<?> statement, Statement<?> task) {
        return new DoStatement(statement, task);
    }

    static ForStatement for_(VariableAssignmentStatement variable, Statement<?> task) {
        return new ForStatement(variable, task);
    }

    static WhileStatement while_(Statement<?> check, Statement<?> task) {
        return new WhileStatement(check, task);
    }

    static IfStatement if_(Statement<?> check, Statement<?> task) {
        return new IfStatement(check, task);
    }

    static IfElseStatement ifElse(Statement<?> check, ChoiceBlockStatement task) {
        return new IfElseStatement(check, task);
    }

    record Property(Statement<?> source, String property) {}

}
