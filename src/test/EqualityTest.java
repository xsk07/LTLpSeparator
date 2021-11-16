package test;

import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;

import static formula.Formula.parseTreeToFormula;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EqualityTest {

    private final Parser parser = new Parser(System.in);

    @Test
    @DisplayName("G(a)")
    void globallyAtom() throws ParseException {
        equalTest("G(a)", "G(a)", true);
    }

    @Test
    @DisplayName("a&b")
    void andOfTwoAtoms() throws ParseException {
        equalTest("a&b", "a&b", true);
        equalTest("a&b", "b&a", true);
    }

    @Test
    @DisplayName("a&...&a")
    void andOfSameAtoms() throws ParseException {
        equalTest("a&a", "a&a", true);
        equalTest("a&a&a", "a&a&a", true);
        equalTest("a&a&a", "a&a", false);
        equalTest("a&a&a", "a", false);
    }

    @Test
    @DisplayName("a&a&b")
    void andOfTwoSameAtomsOneDifferentAtom() throws ParseException {
        equalTest("a&a&b", "a&a&b", true);
        equalTest("a&a&b", "a&b&a", true);
        equalTest("a&a&b", "b&a&a", true);
        equalTest("a&a&b", "a&a&a", false);
        equalTest("a&a&b", "a&b&b", false);
        equalTest("a&a&b", "b&a&b", false);
        equalTest("a&a&b", "b&b&a", false);
    }

    @Test
    @DisplayName("a&b&c")
    void andOfThreeAtoms() throws ParseException {
        equalTest("a&b&c", "a&b&c", true);
        equalTest("a&b&c", "a&c&b", true);
        equalTest("a&b&c", "c&b&a", true);
        equalTest("a&b&c", "b&a&c", true);
    }

    @Test
    @DisplayName("G(a)&G(b)")
    void andOfTwoG() throws ParseException {
        equalTest("G(a)&G(b)", "G(a)&G(b)", true);
        equalTest("G(a)&G(b)", "G(b)&G(a)", true);
    }

    @Test
    @DisplayName("G(a)&F(b)")
    void andOfGaFb() throws ParseException {
        equalTest("G(a)&F(b)", "G(a)&F(b)", true);
        equalTest("G(a)&F(b)", "F(b)&G(a)", true);
    }

    private void equalTest(String s1, String s2, boolean v) throws ParseException {
        Formula f1 = parseFormula(s1);
        Formula f2 = parseFormula(s2);
        compareFormulae(f1, f2, v);
    }

    private Formula parseFormula(String str) throws ParseException {
        byte[] formulaBytes = str.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        parser.ReInit(formulaStream);
        SimpleNode parseTree = parser.Input();
        return parseTreeToFormula(parseTree);
    }

    private void compareFormulae(Formula f1, Formula f2, boolean v) throws IllegalArgumentException {
        assertEquals(v, f1.equalTo(f2));
    }






}
