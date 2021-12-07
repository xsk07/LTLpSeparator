package test;

import converter.FormulaConverter;
import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;

/** Test of the conversion of the unary temporal operators and the until one inside
 *  the formulas, on some standard DECLARE templates. */
class FormulaConverterTest {

    @Test
    @DisplayName("Participation")
    void participation() throws ParseException, IllegalArgumentException {
        templateTest(
                "Fa",
                "trueUa"
        );
    }

    @Test
    @DisplayName("End")
    void end() throws ParseException, IllegalArgumentException {
        templateTest(
                "GFa",
                "!(trueU!(trueUa))"
        );
    }

    @Test
    @DisplayName("AtMostOne")
    void atMostOne() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->X(!Fa))",
                "!(trueU!(!a|(falseU!(trueUa))))"
        );
    }

    @Test
    @DisplayName("RespondedExistence")
    void respondedExistence() throws ParseException, IllegalArgumentException {
        templateTest(
                "Fa -> Fb",
                "!(trueUa)|(trueUb)"
        );
    }

    @Test
    @DisplayName("Response")
    void response() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->Fb)",
                "!(!(a->(bUtrue))Utrue)"
        );
    }

    @Test
    @DisplayName("AlternateResponse")
    void alternateResponse() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->Fb)&G(a->X(!aWb))",
                "!(trueU!(!a|(trueUb)))&!(trueU!(!a|(falseU((!aUb)|!(trueU!!a)))))"
        );
    }

    @Test
    @DisplayName("ChainResponse1")
    void chainResponse1() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->Fb)&G(a->Xb)",
                "!(!(a->(bUtrue))Utrue)&!(!(a->(bUfalse))Utrue)"
        );
    }

    @Test
    @DisplayName("CoExistence1")
    void coExistence1() throws ParseException, IllegalArgumentException {
        templateTest(
                "(Fa&Fb)|(!Fa&!Fb)",
                "((aUtrue)&(bUtrue))|(!(aUtrue)&!(bUtrue))"
        );
    }

    @Test
    @DisplayName("CoExistence2")
    void coExistence2() throws ParseException, IllegalArgumentException {
        templateTest(
                "Fa<->Fb",
                "(aUtrue)<->(bUtrue)"
        );
    }

    @Test
    @DisplayName("NotCoExistence")
    void notCoExistence() throws ParseException, IllegalArgumentException {
        templateTest(
                "G(a->!Fb)&G(b->!Fa)",
                "!(!(a->!(bUtrue))Utrue)&!(!(b->!(aUtrue))Utrue)"
        );
    }

    @Test
    @DisplayName("Precedence")
    void precedence() throws ParseException, IllegalArgumentException {
        templateTest(
                "(!bUa)|G(!b)",
                "(!bUa)|!(!!bUtrue)"
        );
    }


    private void templateTest(String template, String expected) throws ParseException, IllegalArgumentException {
        byte[] templateBytes = template.getBytes();
        ByteArrayInputStream templateStream = new ByteArrayInputStream(templateBytes);
        System.setIn(templateStream);
        Parser parser = new Parser(System.in);
        SimpleNode templateTree = parser.Input();
        Formula templateFormula = templateTree.fromSimpleNodeToFormula();
        FormulaConverter c = new FormulaConverter();
        Formula templateConverted = c.convert(templateFormula);
        assertEquals(expected, templateConverted.toString());
    }

}