package test;

import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;
import static formula.Formula.parseTreeToFormula;
import static formula.Operator.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static separator.eliminator.SubformulaePatterns.*;

class SubformulaePatternsTest {

    private final Parser parser = new Parser(System.in);

    @Test
    @DisplayName("Pattern1")
    void pattern1() throws ParseException {
        Formula pattern_formula = subformulaPattern1(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("qSa");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern2")
    void pattern2() throws ParseException {
        Formula pattern_formula = subformulaPattern2(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("cSa");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern3")
    void pattern3() throws ParseException {
        Formula pattern_formula = subformulaPattern3(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("cUb");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern4")
    void pattern4() throws ParseException {
        Formula pattern_formula = subformulaPattern4(standardFormulaeArray());
        Formula expected_formula = parseFormula("!q & !a");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern5")
    void pattern5() throws ParseException {
        Formula pattern_formula = subformulaPattern5(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("b | (c & cUb)");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern6")
    void pattern6() throws ParseException {
        Formula pattern_formula = subformulaPattern6(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("(!a & !((!a & !b)S(!q & !a) & !b & !c))Sa");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern7")
    void pattern7() throws ParseException {
        Formula pattern_formula = subformulaPattern7(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("(!a & !b)S(!q & !a)");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern8")
    void pattern8() throws ParseException {
        Formula pattern_formula = subformulaPattern8(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("(q & !b)Sa");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern9")
    void pattern9() throws ParseException {
        Formula pattern_formula = subformulaPattern9(standardFormulaeArray());
        Formula expected_formula = parseFormula("!b & !c");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern10")
    void pattern10() throws ParseException {
        Formula pattern_formula = subformulaPattern10(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("!(!b & !c & !bS!q)S(b & cSa)");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern11")
    void pattern11() throws ParseException {
        Formula pattern_formula = subformulaPattern11(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("!bS!q");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern12")
    void pattern12() throws ParseException {
        Formula pattern_formula = subformulaPattern12(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("q | cUb");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern13")
    void pattern13() throws ParseException {
        Formula pattern_formula = subformulaPattern13(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("q | !(cUb)");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern14")
    void pattern14() throws ParseException {
        Formula pattern_formula = subformulaPattern14(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("(q & c)Sa");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern15")
    void pattern15() throws ParseException {
        Formula pattern_formula = subformulaPattern15(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("!a | cUb");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern16")
    void pattern16() throws ParseException {
        Formula pattern_formula = subformulaPattern16(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("!q & cUb");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern17")
    void pattern17() throws ParseException {
        Formula pattern_formula = subformulaPattern17(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("!a & !q & cUb");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern18")
    void pattern18() throws ParseException {
        Formula pattern_formula = subformulaPattern18(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("(!a & c)S(!q & !a)");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("Pattern19")
    void pattern19() throws ParseException {
        Formula pattern_formula = subformulaPattern19(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("!b & !c & (q & !b)Sa");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("cTest")
    void cTest() throws ParseException {
        Formula pattern_formula = new_c(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("(!a & !b)S(!q & !a) & !b & !c");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("b0Test")
    void b0Test() throws ParseException {
        Formula pattern_formula = new_b0(standardFormulaeArray(), SINCE);
        Formula expected_formula = parseFormula("!b & !c & !bS!q");
        compareFormulae(pattern_formula,expected_formula);
    }

    @Test
    @DisplayName("dTest")
    void dTest() throws ParseException {
        Formula pattern_formula = new_d(standardFormulaeArray(), UNTIL);
        Formula expected_formula = parseFormula("b | (c & cUb)");
        compareFormulae(pattern_formula,expected_formula);
    }

    private Formula[] standardFormulaeArray() throws ParseException {
        return parseFormulaeArray(new String[] {"a", "b", "c", "q"});
    }

    private Formula[] parseFormulaeArray(String[] strings) throws ParseException {
        Formula[] subformulae = new Formula[4];
        for (int i = 0; i < strings.length; i++) {
            subformulae[i] = parseFormula(strings[i]);
        }
        return subformulae;
    }

    private Formula parseFormula(String str) throws ParseException {
        byte[] formulaBytes = str.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        parser.ReInit(formulaStream);
        SimpleNode parseTree = parser.Input();
        return parseTreeToFormula(parseTree);
    }

    private void compareFormulae(Formula f1, Formula f2) throws IllegalArgumentException {
        assertTrue(f1.equalTo(f2));
    }

}
