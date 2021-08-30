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
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static translator.Translator.fromSimpleNodeToFormula;
import static separator.FormulaSeparator.*;

class FormulaSeparatorTest {

    @Test
    @DisplayName("NestingCaseSinceCode")
    void nestingCaseSinceCode() throws ParseException, IllegalArgumentException {
        codeTest("(a&(bUc))Sq", 1);
        codeTest("((a&!(bUc))Sq)", 2);
        codeTest("aS(q|(bUc))", 3);
        codeTest("aS(q|!(bUc))", 4);
        codeTest("(a&(bUc))S(q|(bUc))",5);
        codeTest("(a&!(bUc))S(q|(bUc))",6);
        codeTest("(a&(bUc))S(q|!(bUc))",7);
        codeTest("(a&!(bUc))S(q|!(bUc))",8);
    }

    @Test
    @DisplayName("NestingCaseUntilCode")
    void nestingCaseUntilCode() throws ParseException, IllegalArgumentException {
        codeTest("(a&(bSc))Uq", 1);
        codeTest("((a&!(bSc))Uq)", 2);
        codeTest("aU(q|(bSc))", 3);
        codeTest("aU(q|!(bSc))", 4);
        codeTest("(a&(bSc))U(q|(bSc))",5);
        codeTest("(a&!(bSc))U(q|(bSc))",6);
        codeTest("(a&(bSc))U(q|!(bSc))",7);
        codeTest("(a&!(bSc))U(q|!(bSc))",8);
    }

    private void codeTest(String template, int c) throws ParseException, IllegalArgumentException {
        byte[] templateBytes = template.getBytes();
        ByteArrayInputStream templateStream = new ByteArrayInputStream(templateBytes);
        System.setIn(templateStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        BinaryFormula templateFormula = (BinaryFormula) fromSimpleNodeToFormula(tree);
        int code = FormulaSeparator.nestingCase(templateFormula);
        assertEquals(code, c);
    }

    @Test
    @DisplayName("UntilSubformulasCase1")
    void untilSubformula1() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&(bUc))Sq", 1);
    }

    @Test
    @DisplayName("UntilSubformulasCase2")
    void untilSubformulas2() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&!(bUc))Sq", 2);
    }
    @Test
    @DisplayName("UntilSubformulasCase3")
    void untilSubformulas3() throws ParseException, IllegalArgumentException {
        subformulasTest("aS(q|(bUc))", 3);
    }

    @Test
    @DisplayName("UntilSubformulasCase4")
    void untilSubformulas4() throws ParseException, IllegalArgumentException {
        subformulasTest("aS(q|!(bUc))", 4);
    }

    @Test
    @DisplayName("UntilSubformulasCase5")
    void untilSubformulas5() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&(bUc))S(q|(bUc))", 5);
    }

    @Test
    @DisplayName("UntilSubformulasCase6")
    void untilSubformulas6() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&!(bUc))S(q|(bUc))", 6);
    }

    @Test
    @DisplayName("UntilSubformulasCase7")
    void untilSubformulas7() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&(bUc))S(q|!(bUc))", 7);
    }

    @Test
    @DisplayName("UntilSubformulasCase8")
    void untilSubformulas8() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&!(bUc))S(q|!(bUc))", 8);
    }

    @Test
    @DisplayName("SinceSubformulasCase1")
    void sinceSubformula1() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&(bSc))Uq", 1);
    }

    @Test
    @DisplayName("SinceSubformulasCase2")
    void sinceSubformulas2() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&!(bSc))Uq", 2);
    }
    @Test
    @DisplayName("SinceSubformulasCase3")
    void sinceSubformulas3() throws ParseException, IllegalArgumentException {
        subformulasTest("aU(q|(bSc))", 3);
    }

    @Test
    @DisplayName("SinceSubformulasCase4")
    void sinceSubformulas4() throws ParseException, IllegalArgumentException {
        subformulasTest("aU(q|!(bSc))", 4);
    }

    @Test
    @DisplayName("SinceSubformulasCase5")
    void sinceSubformulas5() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&(bSc))U(q|(bSc))", 5);
    }

    @Test
    @DisplayName("SinceSubformulasCase6")
    void SinceSubformulas6() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&!(bSc))U(q|(bSc))", 6);
    }

    @Test
    @DisplayName("SinceSubformulasCase7")
    void sinceSubformulas7() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&(bSc))U(q|!(bSc))", 7);
    }

    @Test
    @DisplayName("SinceSubformulasCase8")
    void sinceSubformulas8() throws ParseException, IllegalArgumentException {
        subformulasTest("(a&!(bSc))U(q|!(bSc))", 8);
    }

    private void subformulasTest(String formula, int c) throws ParseException, IllegalArgumentException {
        byte[] formulaBytes = formula.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        System.setIn(formulaStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        BinaryFormula treeFormula = (BinaryFormula) fromSimpleNodeToFormula(tree);
        ArrayList<Formula> al = new ArrayList<>();
        switch (c){
            case 1:
                al = subformulas1(treeFormula);
                break;
            case 2:
                al = subformulas2(treeFormula);
                break;
            case 3:
                al = subformulas3(treeFormula);
                break;
            case 4:
                al = subformulas4(treeFormula);
                break;
            case 6:
                al = subformulas6(treeFormula);
                break;
            case 5:
            case 7:
                al = subformulas57(treeFormula);
                break;
            case 8:
                al = subformulas8(treeFormula);
                break;
            default: break;
        }
        assertEquals("[a, b, c, q]", al.toString());
    }









}