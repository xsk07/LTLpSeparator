package test;

import formula.BinaryFormula;
import formula.Formula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static separator.FormulaSeparator.getSubformulas;

class SubformulasTest {

    @Test
    @DisplayName("SubformulasCase1")
    void subformulasCase1() throws ParseException, IllegalArgumentException {
        subformulasTest("qS(a&(bUc))",1);
        subformulasTest("qU(a&(bSc))",1);
    }

    @Test
    @DisplayName("SubformulasCase2")
    void subformulasCase2() throws ParseException, IllegalArgumentException {
        subformulasTest("qS(a&!(bUc))",2);
        subformulasTest("qU(a&!(bSc))",2);
    }

    @Test
    @DisplayName("subformulasCase3")
    void subformulas3() throws ParseException, IllegalArgumentException {
        subformulasTest("(q|(bUc))Sa",3);
        subformulasTest("(q|(bSc))Ua",3);
    }

    @Test
    @DisplayName("SubformulasCase4")
    void subformulasCase4() throws ParseException, IllegalArgumentException {
        subformulasTest("(q|!(bUc))Sa",4);
        subformulasTest("(q|!(bSc))Ua",4);
    }

    @Test
    @DisplayName("SubformulasCase5")
    void subformulasCase5() throws ParseException, IllegalArgumentException {
        subformulasTest("(q|(bUc))S(a&(bUc))",5);
        subformulasTest("(q|(bSc))U(a&(bSc))",5);
    }

    @Test
    @DisplayName("SubformulasCase6")
    void subformulasCase6() throws ParseException, IllegalArgumentException {
        subformulasTest("(q|(bUc))S(a&!(bUc))",6);
        subformulasTest("(q|(bSc))U(a&!(bSc))",6);
    }

    @Test
    @DisplayName("SubformulasCase7")
    void subformulasCase7() throws ParseException, IllegalArgumentException {
        subformulasTest("(q|!(bUc))S(a&(bUc))",7);
        subformulasTest("(q|!(bSc))U(a&(bSc))",7);
    }

    @Test
    @DisplayName("SubformulasCase8")
    void subformulasCase8() throws ParseException, IllegalArgumentException {
        subformulasTest("(q|!(bUc))S(a&!(bUc))", 8);
        subformulasTest("(q|!(bSc))U(a&!(bSc))", 8);
    }

    private void subformulasTest(String formula, int c) throws ParseException, IllegalArgumentException {
        byte[] formulaBytes = formula.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        System.setIn(formulaStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        BinaryFormula treeFormula = (BinaryFormula) tree.fromSimpleNodeToFormula();
        Formula[] sfs = getSubformulas(treeFormula, c);
        assertEquals("[a, c, b, q]", Arrays.stream(sfs).toList().toString());
    }

}