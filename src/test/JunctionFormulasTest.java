package test;

import formula.BinaryFormula;
import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;
import static separator.FormulaSeparator.searchX;

class JunctionFormulasTest {

    @Test
    @DisplayName("Formula1")
    void formula1() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX("(q&!(!rStrue))Utrue", "(q&!(!rStrue))Utrue");
        testY(x, "!rStrue");
    }

    @Test
    @DisplayName("Formula2")
    void formula2() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX("a&!(!!(!qStrue)Utrue)", "!!(!qStrue)Utrue");
        testY(x, "!qStrue");
    }

    @Test
    @DisplayName("Formula3")
    void formula3() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX("!(trueUq)Strue", "!(trueUq)Strue");
        testY(x, "trueUq");
    }

    @Test
    @DisplayName("Formula4")
    void formula4() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX("!(!!(!falseStrue)Utrue)|a", "!!(!falseStrue)Utrue");
        testY(x, "!falseStrue");
    }


    @Test
    @DisplayName("Formula5")
    void formula5() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX(
                "!!(!(!a|((a|b)Sfalse))Utrue)|!a|(bUtrue)",
                "!(!a|((a|b)Sfalse))Utrue");
        testY(x, "(a|b)Sfalse");
    }


    @Test
    @DisplayName("Formula6")
    void formula6() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX(
                "!(!(!(!a|((a|b)Sfalse))Utrue)&(!b|(falseStrue)))|!a|(bUtrue)",
                "!(!a|((a|b)Sfalse))Utrue");
        testY(x, "(a|b)Sfalse");
    }

    @Test
    @DisplayName("Formula7")
    void formula7() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX(
                "!(!(!(!(aSfalse)&b)|c)Utrue)",
                "!(!(!(aSfalse)&b)|c)Utrue");
        testY(x, "aSfalse");
    }

    @Test
    @DisplayName("Formula8")
    void formula8() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX(
                "!(!(!p|(!(!!pStrue)Sfalse))Utrue)",
                "!(!p|(!(!!pStrue)Sfalse))Utrue");
        testY(x, "!(!!pStrue)Sfalse");
    }

    @Test
    @DisplayName("Formula9")
    void formula9() throws ParseException, IllegalArgumentException {
        BinaryFormula x = testX(
                "!(!(!(shoot&(shootStrueSfalse)&!(!unloadedStrue))|(failUtrue))Utrue)",
                "!(!(shoot&(shootStrueSfalse)&!(!unloadedStrue))|(failUtrue))Utrue");
        testY(x, "shootStrueSfalse");
    }

    private BinaryFormula testX(String f, String ex) throws ParseException {
        byte[] templateBytes = f.getBytes();
        ByteArrayInputStream fStrm = new ByteArrayInputStream(templateBytes);
        System.setIn(fStrm);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        Formula ft = tree.fromSimpleNodeToFormula();
        BinaryFormula x = searchX(ft);
        assertEquals(x.toString(), ex);
        return x;
    }

    private void testY(BinaryFormula x, String ey) {
        assertEquals(x.searchOperator(x.getOperator().getMirrorOperator()).toString(), ey);
    }

}