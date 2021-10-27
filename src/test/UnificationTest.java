package test;
import formula.BinaryFormula;
import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.FormulaSeparator;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;

class UnificationTest {

    private FormulaSeparator separator = new FormulaSeparator();

    @Test
    @DisplayName("ANDSameChildren")
    void andSameChildren() throws ParseException {
        unifyTest("(aSb)&(aSb)","aSb");
    }

    @Test
    @DisplayName("ANDSameNegChildren")
    void andSameNegChildren() throws ParseException {
        unifyTest("!(aSb)&!(aSb)","!(aSb)");
    }

    @Test
    @DisplayName("ORSameChildren")
    void orSameChildren() throws ParseException {
        unifyTest("(aSb)|(aSb)","aSb");
    }

    @Test
    @DisplayName("ORSameNegChildren")
    void orSameNegChildren() throws ParseException {
        unifyTest("!(aSb)|!(aSb)","!(aSb)");
    }

    @Test
    @DisplayName("ANDOneNegationOfTheOther1")
    void andOneNegationOfTheOther1() throws ParseException {
        unifyTest("(aSb)&!(aSb)","false");
    }

    @Test
    @DisplayName("ANDOneNegationOfTheOther2")
    void andOneNegationOfTheOther2() throws ParseException {
        unifyTest("!(aSb)&(aSb)","false");
    }

    @Test
    @DisplayName("OROneNegationOfTheOther1")
    void orOneNegationOfTheOther1() throws ParseException {
        unifyTest("(aSb)|!(aSb)","true");
    }

    @Test
    @DisplayName("OROneNegationOfTheOther2")
    void orOneNegationOfTheOther2() throws ParseException {
        unifyTest("!(aSb)|(aSb)","true");
    }

    @Test
    @DisplayName("CaseOrLemmaA2_1")
    void caseOrLemmaA2_1() throws ParseException {
        unifyTest("(aUc)|(bUc)", "(a|b)Uc");
    }

    @Test
    @DisplayName("CaseOrLemmaA2_2")
    void caseOrLemmaA2_2() throws ParseException {
        unifyTest("(aSc)|(bSc)", "(a|b)Sc");
    }

    @Test
    @DisplayName("CaseAndLemmaA2_1")
    void caseAndLemmaA2_1() throws ParseException {
        unifyTest("(aUb)&(aUc)", "aU(b&c)");
    }

    @Test
    @DisplayName("CaseAndLemmaA2_2")
    void caseAndLemmaA2_2() throws ParseException {
        unifyTest("(aSb)&(aSc)", "aS(b&c)");
    }

    @Test
    @DisplayName("CaseAndLemmaA1_1")
    void caseAndLemmaA1_1() throws ParseException {
        unifyTest(
                "(aUb)&(cUd)",
                "((a&c)U(b&d))|((a&d&(cUd))U(b&d))|((c&b&(aUb))U(b&d))");
    }

    @Test
    @DisplayName("CaseAndLemmaA1_2")
    void caseAndLemmaA1_2() throws ParseException {
        unifyTest(
                "(aSb)&(cSd)",
                "((a&c)S(b&d))|((a&d&(cSd))S(b&d))|((c&b&(aSb))S(b&d))");
    }

    @Test
    @DisplayName("DifferentAndNegated1")
    void differentAndNegated1() throws ParseException {
        unifyTest(
                "!(aSb)&!(cSd)",
                "!(((a&c)S(b&d))|((a&d&(cSd))S(b&d))|((c&b&(aSb))S(b&d)))");
    }

    private void unifyTest(String formula, String expectedFormula) throws ParseException, IllegalArgumentException {
        byte[] formulaBytes = formula.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        System.setIn(formulaStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        BinaryFormula treeFormula = (BinaryFormula) tree.fromSimpleNodeToFormula();
        Formula unifiedFormula = separator.unify(treeFormula);
        assertEquals(expectedFormula, unifiedFormula.toString());
    }

}