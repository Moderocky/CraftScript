package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.*;

import java.util.function.Supplier;

public class BukkitScriptLoader extends SimpleScriptLoader {

    public static final ScriptLoader BASIC = new BukkitScriptLoader(
        SimpleScriptLoader.BASIC,
        NullParser::new,
        AssertParser::new,
        ForParser::new,
        DoParser::new,
        IfParser::new,
        WhileParser::new,
        FunctionParser::new,
        StructParser::new,
        RequireParser::new,
        ImportParser::new,
        ListenerParser::new,
        RunParser::new,
        SyntaxParser::new,
        ElseParser::new,
        InvertParser::new,
        BooleanParser::new,
        SelectorParser::new,
        ScriptParser::new,
        BlockParser::new,
        MapParser::new,
        ListParser::new,
        StringParser::new,
        CommandParser::new,
        CommentParser::new,
        InterpolationParser::new,
        ResourceParser::new,
        CloseParser::new,
        IntegerParser::new,
        DoubleParser::new,
        VariableAssignmentParser::new,
        BinaryParser::compareEQ,
        BinaryParser::comparePlus,
        BinaryParser::compareMinus,
        BinaryParser::compareTimes,
        BinaryParser::compareDivide,
        BinaryParser::compareLE,
        BinaryParser::compareGE,
        BinaryParser::compareLT,
        BinaryParser::compareGT,
        BinaryParser::compareNE,
        BinaryParser::compareAND,
        BinaryParser::compareOR,
        BinaryParser::compareXOR,
        BinaryParser::compareALT,
        SetterParser::new,
        GetterParser::new,
        KindParser::new,
        VariableParser::new
    );

    @SafeVarargs
    public BukkitScriptLoader(SimpleScriptLoader loader, Supplier<Parser>... parsers) {
        super(loader, parsers);
    }

    @SafeVarargs
    public BukkitScriptLoader(Supplier<Parser>... parsers) {
        this.parsers = parsers;
    }

}
