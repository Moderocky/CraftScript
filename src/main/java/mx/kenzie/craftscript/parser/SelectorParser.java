package mx.kenzie.craftscript.parser;

import mx.kenzie.centurion.selector.Criterion;
import mx.kenzie.centurion.selector.Finder;
import mx.kenzie.centurion.selector.Selector;
import mx.kenzie.centurion.selector.Universe;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.InterpolationStatement;
import mx.kenzie.craftscript.statement.SelectorStatement;
import mx.kenzie.craftscript.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class SelectorParser extends BasicParser {

    private static final Universe<?> universe = Universe.of(Finder.ALL_ENTITIES, Finder.ALL_PLAYERS, Finder.PLAYER,
        Finder.RANDOM_PLAYER, Finder.SENDER, Criterion.ENTITY_DISTANCE, Criterion.ENTITY_TYPE, Criterion.LIMIT,
        Criterion.GAME_MODE, Criterion.LEVEL, Criterion.X_ROTATION, Criterion.Y_ROTATION);
    private int start;

    protected Universe<?> getUniverse() {
        return universe;
    }

    @Override
    public boolean matches() {
        if (!input.startsWith("@")) return false;
        return input.length() > 1;
    }

    @Override
    public Statement<?> parse() throws ScriptError {
        final Universe<?> universe = this.getUniverse();
        if (input.indexOf('}') > input.indexOf('{')) {
            this.start = 0;
            final List<InterpolationStatement> list = new ArrayList<>();
            do {
                final int open = input.indexOf('{', start), close = input.indexOf('}', open);
                if (open < 0 || close < 0) break;
                final String value = input.substring(open, close + 1);
                this.start = close;
                final Statement<?> statement = parent.parse(value);
                if (statement instanceof InterpolationStatement interpolation) list.add(interpolation);
            } while (true);
            return new SelectorStatement(input, universe, list.toArray(new InterpolationStatement[0]));
        } else if (Selector.validate(input, universe)) return new SelectorStatement(input, this.getUniverse());
        else throw new ScriptError("The selector '" + input + "' is invalid.");
    }

    @Override
    public void close() throws ScriptError {
        this.start = 0;
        super.close();
    }

}
