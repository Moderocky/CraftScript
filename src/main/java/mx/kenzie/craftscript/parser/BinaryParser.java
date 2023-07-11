package mx.kenzie.craftscript.parser;

import mx.kenzie.craftscript.statement.CompareStatement;
import mx.kenzie.craftscript.utility.Comparator;

public class BinaryParser extends OperatorParser {

    public BinaryParser(Comparator comparator) {
        super(comparator.toString(),
            (statement, statement2) -> new CompareStatement(statement, statement2, comparator));
    }

    public static OperatorParser compareEQ() {
        return new EqualsParser();
    }

    public static OperatorParser comparePlus() {
        return new BinaryParser(Comparator.PLUS);
    }

    public static OperatorParser compareMinus() {
        return new BinaryParser(Comparator.MINUS);
    }

    public static OperatorParser compareTimes() {
        return new BinaryParser(Comparator.TIMES);
    }

    public static OperatorParser compareDivide() {
        return new BinaryParser(Comparator.DIVIDE);
    }

    public static BinaryParser compareLT() {
        return new BinaryParser(Comparator.LESS_THAN);
    }

    public static BinaryParser compareLE() {
        return new BinaryParser(Comparator.LESS_EQUAL);
    }

    public static BinaryParser compareGT() {
        return new BinaryParser(Comparator.GREATER_THAN);
    }

    public static BinaryParser compareGE() {
        return new BinaryParser(Comparator.GREATER_EQUAL);
    }

    public static BinaryParser compareAND() {
        return new BinaryParser(Comparator.AND);
    }

    public static BinaryParser compareOR() {
        return new BinaryParser(Comparator.OR);
    }

    public static BinaryParser compareXOR() {
        return new BinaryParser(Comparator.XOR);
    }

    public static BinaryParser compareNE() {
        return new BinaryParser(Comparator.NE);
    }

    public static BinaryParser compareALT() {
        return new BinaryParser(Comparator.ALT);
    }

}
