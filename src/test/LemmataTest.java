package test;

import formula.BinaryFormula;
import formula.Formula;
import formula.UnaryFormula;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.Lemmata;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static formula.Formula.parseTreeToFormula;
import static org.junit.jupiter.api.Assertions.*;
import static separator.FormulaSeparator.applyReversedLemmaA2;

class LemmataTest {

    private final Parser parser = new Parser(System.in);

    @Test
    void reversedLemmaA2Test1() throws ParseException {
        BinaryFormula f = (BinaryFormula) parseFormula("aUb & aUb & f & aUc & aSb & cUd & cUe & bUa & aSc & a ");
        Formula lemmaF = applyReversedLemmaA2(f);
        System.out.println(lemmaF);
    }

    @Test
    void lemmaA1ThenLemmaA2_Test1() throws ParseException {
        BinaryFormula f1 = (BinaryFormula) parseFormula("aUb");
        BinaryFormula f2 = (BinaryFormula) parseFormula("cUd");
        BinaryFormula lemmaF = Lemmata.lemmaA1(f1, f2);
        System.out.println(lemmaF);
        ArrayList<?> operands = lemmaF.getCombinationOperands();
        BinaryFormula newTempFormula = Lemmata.reversedLemmaA2((ArrayList<BinaryFormula>) operands);
        System.out.println(newTempFormula);
    }

    @Test
    void lemmaA1Test2() throws ParseException {
        UnaryFormula notF1 = (UnaryFormula) parseFormula("!(aUb)");
        UnaryFormula notF2 = (UnaryFormula) parseFormula("!(cUd)");
        BinaryFormula f1 = (BinaryFormula) notF1.getOperand();
        BinaryFormula f2 = (BinaryFormula) notF2.getOperand();
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