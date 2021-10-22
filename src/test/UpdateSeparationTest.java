package test;

import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;
import static converter.FormulaConverter.convert;
import static org.junit.jupiter.api.Assertions.*;

class UpdateSeparationTest {

    @Test
    @DisplayName("Atoms")
    void atomsTest() throws ParseException {
        separationTest("a", true);
    }

    @Test
    @DisplayName("BooleanOperationOfSeparated")
    void boolOfSeparated() throws ParseException {
        separationTest("a & b", true);
        separationTest("a | b", true);
        separationTest("a -> b", true);
        separationTest("a <-> b", true);
        separationTest("!a", true);
    }

    @Test
    @DisplayName("e1")
    void e1Test() throws ParseException {
        separationTest("(a&(bUc))Sq", false);
    }

    @Test
    @DisplayName("e2")
    void e2Test() throws ParseException {
        separationTest("(a&!(bUc))Sq", false);
    }

    @Test
    @DisplayName("e3")
    void e3Test() throws ParseException {
        separationTest("aS(q|(bUc))", false);
    }

    @Test
    @DisplayName("e4")
    void e4Test() throws ParseException {
        separationTest("aS(q|!(bUc))", false);
    }

    @Test
    @DisplayName("e5")
    void e5Test() throws ParseException {
        separationTest("(a&(bUc))S(q|(bUc))", false);
    }

    @Test
    @DisplayName("e6")
    void e6Test() throws ParseException {
        separationTest("(a&!(bUc))S(q|(bUc))", false);
    }

    @Test
    @DisplayName("e7")
    void e7Test() throws ParseException {
        separationTest("(a&(bUc))S(q|!(bUc))", false);
    }

    @Test
    @DisplayName("e8")
    void e8Test() throws ParseException {
        separationTest("(a&!(bUc))S(q|!(bUc))", false);
    }

    @Test
    @DisplayName("ParticipationTest")
    void participationTest() throws ParseException {
        separationTest("Fa", true);
    }

    @Test
    @DisplayName("AtMostOneTest")
    void atMostOneTestTest() throws ParseException {
        separationTest("G(a->X(!Fa))", true);
    }

    @Test
    @DisplayName("RespondedExistence")
    void respondedExistenceTest() throws ParseException {
        separationTest("Fa -> Fb", true);
    }

    @Test
    @DisplayName("Response")
    void responseTest() throws ParseException {
        separationTest("G(a->Fb)", true);
    }


    @Test
    @DisplayName("AlternateResponse")
    void alternateResponseTest() throws ParseException {
        separationTest("G(a->Fb)&G(a->X(!aWb))", true);
    }

    @Test
    @DisplayName("ChainResponse1")
    void chainResponseTest1() throws ParseException {
        separationTest("G(a->Fb)&G(a->Xb)", true);
    }

    @Test
    @DisplayName("CoExistence1")
    void coExistenceTest1() throws ParseException {
        separationTest("(Fa&Fb)|(!Fa&!Fb)", true);
    }

    @Test
    @DisplayName("CoExistence2")
    void coExistenceTest2() throws ParseException {
        separationTest("Fa<->Fb", true);
    }

    @Test
    @DisplayName("NotCoExistence")
    void notCoExistenceTest() throws ParseException {
        separationTest("G(a->!Fb)&G(b->!Fa)", true);
    }

    @Test
    @DisplayName("Precedence")
    void precedenceTest() throws ParseException {
        separationTest("(!bUa)|G(!b)", true);
    }

    @Test
    @DisplayName("x1")
    void x1Test() throws ParseException {
        separationTest("(bUc)Sq", false);
    }

    @Test
    @DisplayName("x2")
    void x2Test() throws ParseException {
        separationTest("!(bUc)Sq", false);
    }

    @Test
    @DisplayName("x3")
    void x3Test() throws ParseException {
        separationTest("qS(bUc)", false);
    }

    @Test
    @DisplayName("x4")
    void x4Test() throws ParseException {
        separationTest("qS!(bUc)", false);
    }

    @Test
    @DisplayName("x5")
    void x5Test() throws ParseException {
        separationTest("(aUb)S(bUc)", false);
    }

    @Test
    @DisplayName("x6")
    void x6Test() throws ParseException {
        separationTest("!(aUb)S(bUc)", false);
    }

    @Test
    @DisplayName("x7")
    void x7Test() throws ParseException {
        separationTest("(aUb)S!(bUc)", false);
    }

    @Test
    @DisplayName("x8")
    void x8Test() throws ParseException {
        separationTest("!(aUb)S!(bUc)", false);
    }

    @Test
    @DisplayName("formula1")
    void formula1() throws ParseException {
        separationTest("F(q & Hr)", false);
    }

    @Test
    @DisplayName("formula2")
    void formula2() throws ParseException {
        separationTest("a & GHq", false);
    }

    @Test
    @DisplayName("formula3")
    void formula3() throws ParseException {
        separationTest("O!(true U q)", false);
    }

    @Test
    @DisplayName("formula4")
    void formula4() throws ParseException {
        separationTest("!(G(H(false))) -> a", false);
    }

    @Test
    @DisplayName("formula5")
    void formula5() throws ParseException {
        separationTest("G(a -> Y(a|b)) -> (a -> Fb)", false);
    }

    @Test
    @DisplayName("formula6")
    void formula6() throws ParseException {
        separationTest("(G(a -> Y(a|b)) & (b -> O false))-> (a -> Fb)", false);
    }

    @Test
    @DisplayName("formula7")
    void formula7() throws ParseException {
        separationTest("G((!Ya & b) -> c)", false);
    }

    @Test
    @DisplayName("formula8")
    void formula8() throws ParseException {
        separationTest("G(p -> YH!p)", false);
    }

    @Test
    @DisplayName("formula9")
    void formula9() throws ParseException {
        separationTest("G((shoot & YO(shoot) & H(unloaded)) -> F(fail)) ", false);
    }


    private void separationTest(String phi, boolean  sep) throws ParseException {
        byte[] phiBytes = phi.getBytes();
        ByteArrayInputStream phiStream = new ByteArrayInputStream(phiBytes);
        System.setIn(phiStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        Formula phiFormula = tree.fromSimpleNodeToFormula();
        Formula phiConverted = convert(phiFormula);
        assertEquals(sep, phiConverted.isSeparated());
    }



}