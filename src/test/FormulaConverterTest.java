package test;

import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static converter.FormulaConverter.convert;
import static org.junit.jupiter.api.Assertions.*;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;

/** Test of the conversion of the unary temporal operators and the until one inside
 * the formulas, on some standard DECLARE templates. */
class FormulaConverterTest {

    @Test
    @DisplayName("Participation")
    void participationTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "Fa",
                "aUtrue"
        );
    }

    @Test
    @DisplayName("End")
    void endTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "GFa",
                "!(!(aUtrue)Utrue)"
        );
    }

    @Test
    @DisplayName("AtMostOne")
    void atMostOneTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->X(!Fa))",
                "!(!(a->(!(aUtrue)Ufalse))Utrue)"
        );
    }

    @Test
    @DisplayName("RespondedExistence")
    void respondedExistenceTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "Fa -> Fb",
                "(aUtrue)->(bUtrue)"
        );
    }

    @Test
    @DisplayName("Response")
    void responseTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->Fb)",
                "!(!(a->(bUtrue))Utrue)"
        );
    }

    @Test
    @DisplayName("AlternateResponse")
    void alternateResponseTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->Fb)&G(a->X(!aWb))",
                "!(!(a->(bUtrue))Utrue)&!(!(a->(((!aUb)|!(!!aUtrue))Ufalse))Utrue)"
        );
    }

    @Test
    @DisplayName("ChainResponse1")
    void chainResponseTest1() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->Fb)&G(a->Xb)",
                "!(!(a->(bUtrue))Utrue)&!(!(a->(bUfalse))Utrue)"
        );
    }

    @Test
    @DisplayName("CoExistence1")
    void coExistenceTest1() throws ParseException, IllegalArgumentException {
        templateTest(
                "(Fa&Fb)|(!Fa&!Fb)",
                "((aUtrue)&(bUtrue))|(!(aUtrue)&!(bUtrue))"
        );
    }

    @Test
    @DisplayName("CoExistence2")
    void coExistenceTest2() throws ParseException, IllegalArgumentException {
        templateTest(
                "Fa<->Fb",
                "(aUtrue)<->(bUtrue)"
        );
    }

    @Test
    @DisplayName("NotCoExistence")
    void notCoExistenceTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->!Fb)&G(b->!Fa)",
                "!(!(a->!(bUtrue))Utrue)&!(!(b->!(aUtrue))Utrue)"
        );
    }

    @Test
    @DisplayName("Precedence")
    void precedenceTest() throws ParseException, IllegalArgumentException {
        templateTest(
                "(!bUa)|G(!b)",
                "(!bUa)|!(!!bUtrue)"
        );
    }


    private void templateTest(String template, String result) throws ParseException, IllegalArgumentException {
        byte[] templateBytes = template.getBytes();
        ByteArrayInputStream templateStream = new ByteArrayInputStream(templateBytes);
        System.setIn(templateStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        Formula templateFormula = tree.fromSimpleNodeToFormula();
        Formula templateConverted = convert(templateFormula);
        assertEquals(templateConverted.toString(), result);
    }

}