package test;

import formula.BinaryFormula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.FormulaSeparator;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;

class NestingCaseTest {

    private final FormulaSeparator separator = new FormulaSeparator();

    @Test
    @DisplayName("NestingCase1")
    void nestingCase1() throws ParseException, IllegalArgumentException {
        nestingCaseTest("qS(a&(bUc))",1);
        nestingCaseTest("qU(a&(bSc))",1);
    }

    @Test
    @DisplayName("NestingCase2")
    void nestingCase2() throws ParseException, IllegalArgumentException {
        nestingCaseTest("qS(a&!(bUc))",2);
        nestingCaseTest("qU(a&!(bSc))",2);
    }

    @Test
    @DisplayName("NestingCase3")
    void nestingCase3() throws ParseException, IllegalArgumentException {
        nestingCaseTest("(q|(bUc))Sa",3);
        nestingCaseTest("(q|(bSc))Ua",3);
    }

    @Test
    @DisplayName("NestingCase4")
    void nestingCase4() throws ParseException, IllegalArgumentException {
        nestingCaseTest("(q|!(bUc))Sa",4);
        nestingCaseTest("(q|!(bSc))Ua",4);
    }

    @Test
    @DisplayName("NestingCase5")
    void nestingCase5() throws ParseException, IllegalArgumentException {
        nestingCaseTest("(q|(bUc))S(a&(bUc))",5);
        nestingCaseTest("(q|(bSc))U(a&(bSc))",5);
    }

    @Test
    @DisplayName("NestingCase6")
    void nestingCase6() throws ParseException, IllegalArgumentException {
        nestingCaseTest("(q|(bUc))S(a&!(bUc))",6);
        nestingCaseTest("(q|(bSc))U(a&!(bSc))",6);
    }

    @Test
    @DisplayName("NestingCase7")
    void nestingCase7() throws ParseException, IllegalArgumentException {
        nestingCaseTest("(q|!(bUc))S(a&(bUc))",7);
        nestingCaseTest("(q|!(bSc))U(a&(bSc))",7);
    }

    @Test
    @DisplayName("NestingCase8")
    void nestingCase8() throws ParseException, IllegalArgumentException {
        nestingCaseTest("(q|!(bUc))S(a&!(bUc))",8);
        nestingCaseTest("(q|!(bSc))U(a&!(bSc))",8);
    }

    private void nestingCaseTest(String f, int c) throws ParseException, IllegalArgumentException {
        byte[] fB= f.getBytes();
        ByteArrayInputStream fS= new ByteArrayInputStream(fB);
        System.setIn(fS);
        Parser parser = new Parser(System.in);
        SimpleNode t = parser.Input();
        BinaryFormula tF = (BinaryFormula) t.fromSimpleNodeToFormula();
        int code = separator.nestingCase(tF);
        assertEquals(c, code);
    }

}