package mx.kenzie.craftscript.script;

import mx.kenzie.craftscript.parser.*;

import java.io.IOException;
import java.io.InputStream;

public interface ScriptLoader extends ScriptParser {

    ScriptLoader BASIC = new SimpleScriptLoader(
        NullParser::new,
        AssertParser::new,
        ForParser::new,
        IfParser::new,
        WhileParser::new,
        FunctionParser::new,
        StructParser::new,
        RequireParser::new,
        ImportParser::new,
        ListenerParser::new,
        RunParser::new,
        ElseParser::new,
        InvertParser::new,
        BooleanParser::new,
        SelectorParser::new,
        mx.kenzie.craftscript.parser.ScriptParser::new,
        BlockParser::new,
        MapParser::new,
        ListParser::new,
        StringParser::new,
        InterpolationParser::new,
        ResourceParser::new,
        KindParser::new,
        CloseParser::new,
        IntegerParser::new,
        DoubleParser::new,
        CommandParser::new,
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
        VariableParser::new
    );

    Script parse(String name, InputStream stream) throws IOException;

    AbstractScript parse(InputStream stream) throws IOException;

}
