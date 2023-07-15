package mx.kenzie.craftscript.parser;

import mx.kenzie.centurion.selector.Criterion;
import mx.kenzie.centurion.selector.Finder;
import mx.kenzie.centurion.selector.Selector;
import mx.kenzie.centurion.selector.Universe;
import mx.kenzie.craftscript.script.ScriptError;
import mx.kenzie.craftscript.statement.SelectorStatement;
import mx.kenzie.craftscript.statement.Statement;
import mx.kenzie.craftscript.utility.Interpolator;

public class SelectorParser extends BasicParser {

    public static final Universe<?> universe = Universe.of(Finder.ALL_ENTITIES, Finder.ALL_PLAYERS, Finder.PLAYER,
        Finder.RANDOM_PLAYER, Finder.SENDER, Criterion.ENTITY_DISTANCE, Criterion.ENTITY_TYPE, Criterion.LIMIT,
        Criterion.GAME_MODE, Criterion.LEVEL, Criterion.X_ROTATION, Criterion.Y_ROTATION);

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
            final Object[] parts = new Interpolator(input, parent).interpolations();
            return new SelectorStatement(input, universe, parts);
        } else if (Selector.validate(input, universe)) return new SelectorStatement(input, this.getUniverse());
        else throw new ScriptError("The selector '" + input + "' is invalid.");
    }

    @Override
    public void close() throws ScriptError {
        super.close();
    }

}
