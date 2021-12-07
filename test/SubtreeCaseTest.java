package test;

import formula.BinaryFormula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.FormulaSeparator;
import java.io.ByteArrayInputStream;
import separator.FormulaSeparator.*;
import static org.junit.jupiter.api.Assertions.*;
import static separator.FormulaSeparator.Directions.*;

class SubtreeCaseTest {

    private final FormulaSeparator separator = new FormulaSeparator();

    // ELIMINATION 1

    @Test
    @DisplayName("LeftSubtreeElimination1")
    void leftSubtreeElimination1() throws ParseException, IllegalArgumentException {
        treeCaseTest("qS(a&(bUc))", 0, LEFT);
        treeCaseTest("qU(a&(bSc))", 0, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination1")
    void rightSubtreeElimination1() throws ParseException, IllegalArgumentException {
        treeCaseTest("qS(a&(bUc))", 1, RIGHT);
        treeCaseTest("qU(a&(bSc))", 1, RIGHT);
    }

    // ELIMINATION 2

    @Test
    @DisplayName("LeftSubtreeElimination2")
    void leftSubtreeElimination2() throws ParseException, IllegalArgumentException {
        treeCaseTest("qS(a&!(bUc))", 0, LEFT);
        treeCaseTest("qU(a&!(bSc))", 0, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination2")
    void rightSubtreeElimination2() throws ParseException, IllegalArgumentException {
        treeCaseTest("qS(a&!(bUc))", 2, RIGHT);
        treeCaseTest("qU(a&!(bSc))", 2, RIGHT);
    }

    // ELIMINATION 3

    @Test
    @DisplayName("LeftSubtreeElimination3")
    void leftSubtreeElimination3() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|(bUc))Sa", 1, LEFT);
        treeCaseTest("(q|(bSc))Ua", 1, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination3")
    void rightSubtreeElimination3() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|(bUc))Sa", 0, RIGHT);
        treeCaseTest("(q|(bUc))Sa", 0, RIGHT);
    }

    // ELIMINATION 4

    @Test
    @DisplayName("LeftSubtreeElimination4")
    void leftSubtreeElimination4() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|!(bUc))Sa", 2, LEFT);
        treeCaseTest("(q|!(bSc))Ua", 2, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination4")
    void rightSubtreeElimination4() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|!(bUc))Sa", 0, RIGHT);
        treeCaseTest("(q|!(bSc))Ua", 0, RIGHT);
    }


    // ELIMINATION 5

    @Test
    @DisplayName("LeftSubtreeElimination5")
    void leftSubtreeElimination5() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|(bUc))S(a&(bUc))", 1, LEFT);
        treeCaseTest("(q|(bSc))U(a&(bSc))", 1, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination5")
    void rightSubtreeElimination5() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|(bUc))S(a&(bUc))", 1, RIGHT);
        treeCaseTest("(q|(bSc))U(a&(bSc))", 1, RIGHT);
    }

    // ELIMINATION 6

    @Test
    @DisplayName("LeftSubtreeElimination6")
    void leftSubtreeElimination6() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|(bUc))S(a&!(bUc))", 1, LEFT);
        treeCaseTest("(q|(bSc))U(a&!(bSc))", 1, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination6")
    void rightSubtreeElimination6() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|(bUc))S(a&!(bUc))", 2, RIGHT);
        treeCaseTest("(q|(bSc))U(a&!(bSc))", 2, RIGHT);
    }

    // ELIMINATION 7

    @Test
    @DisplayName("LeftSubtreeElimination7")
    void leftSubtreeElimination7() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|!(bUc))S(a&(bUc))", 2, LEFT);
        treeCaseTest("(q|!(bSc))U(a&(bSc))", 2, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination7")
    void rightSubtreeElimination7() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|!(bUc))S(a&(bUc))", 1, RIGHT);
        treeCaseTest("(q|!(bSc))U(a&(bSc))", 1, RIGHT);
    }

    // ELIMINATION 8

    @Test
    @DisplayName("LeftSubtreeElimination8")
    void leftSubtreeElimination8() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|!(bUc))S(a&!(bUc))", 2, LEFT);
        treeCaseTest("(q|!(bSc))U(a&!(bSc))", 2, LEFT);
    }

    @Test
    @DisplayName("RightSubtreeElimination8")
    void rightSubtreeElimination8() throws ParseException, IllegalArgumentException {
        treeCaseTest("(q|!(bUc))S(a&!(bUc))", 2, RIGHT);
        treeCaseTest("(q|!(bSc))U(a&!(bSc))", 2, RIGHT);
    }


    private void treeCaseTest(String f, int c, Directions d) throws ParseException, IllegalArgumentException {
        byte[] fB= f.getBytes();
        ByteArrayInputStream fS= new ByteArrayInputStream(fB);
        System.setIn(fS);
        Parser parser = new Parser(System.in);
        SimpleNode t = parser.Input();
        BinaryFormula tF = (BinaryFormula) t.fromSimpleNodeToFormula();
        int code = separator.subtreeCase(tF, d);
        assertEquals(c, code);
    }

}