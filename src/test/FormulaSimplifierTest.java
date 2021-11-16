package test;

import formula.BinaryFormula;
import formula.Formula;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;

import static formula.Formula.parseTreeToFormula;
import static org.junit.jupiter.api.Assertions.*;
import static simplifier.FormulaSimplifier.simplify;

class FormulaSimplifierTest {

    private final Parser parser = new Parser(System.in);

    @Test
    void simplifyTest1() throws ParseException {
        BinaryFormula f = (BinaryFormula) parseFormula("!(dUc) & a & b & bUa & !(bUa) & !b & a & dUc & dUc");
        Formula simplified_f = simplify(f);
        Formula expected_f = parseFormula("a & false");
        compareFormulae(simplified_f, expected_f);
    }

    @Test
    void simplifyTest2() throws ParseException {
        BinaryFormula f = (BinaryFormula) parseFormula("aUb & !(aUb) & aUb & !!(aUb) & aUb");
        Formula simplified_f = simplify(f);
        Formula expected_f = parseFormula("false");
        compareFormulae(simplified_f, expected_f);
    }


    @Test
    void simplifyTest3() throws ParseException {
        BinaryFormula f = (BinaryFormula) parseFormula("aUb | !(aUb) | aUb | !!(aUb) | aUb");
        Formula simplified_f = simplify(f);
        Formula expected_f = parseFormula("true");
        compareFormulae(simplified_f, expected_f);
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