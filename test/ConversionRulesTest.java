package test;

import converter.FormulaConverter;
import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class ConversionRulesTest {

    private final FormulaConverter converter = new FormulaConverter();

    @Test
    @DisplayName("OnceRule")
    void ruleO() throws ParseException {
        ruleTest("Oq", "trueSq");
    }

    @Test
    @DisplayName("HistoricallyRule")
    void ruleH() throws ParseException {
        ruleTest("Hq", "!(trueS!q)");
    }

    @Test
    @DisplayName("YesterdayRule")
    void ruleY() throws ParseException {
        ruleTest("Yq", "falseSq");

    }

    @Test
    @DisplayName("FutureRule")
    void ruleF() throws ParseException {
        ruleTest("Fq", "trueUq");
    }

    @Test
    @DisplayName("NextRule")
    void ruleX() throws ParseException {
        ruleTest("Xq", "falseUq");
    }

    @Test
    @DisplayName("GloballyRule")
    void ruleG() throws ParseException {
        ruleTest("Gq", "!(trueU!q)");
    }

    @Test
    @DisplayName("UnlessRule")
    // p W q =>* (p U q) | G p
    void ruleW() throws ParseException {
        ruleTest("pWq", "(pUq)|!(trueU!p)");
    }

    private void ruleTest(String formula, String expected) throws ParseException, IllegalArgumentException {
        byte[] formulaBytes = formula.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        System.setIn(formulaStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        Formula treeFormula = tree.fromSimpleNodeToFormula();
        Formula convertedFormula = converter.convert(treeFormula);
        assertEquals(expected, convertedFormula.toString());
    }

}