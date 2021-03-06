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


class FormulaEliminatorTest {

    private FormulaSeparator separator = new FormulaSeparator();

    @Test
    @DisplayName("Elimination1")
    void elimination1() throws ParseException, IllegalArgumentException {
        separationTest(
                "(a&(bUc))Sq",
                "((aSq)&(aSc)&c&(bUc))|(b&(aS(q&c)))|((b&q&(aSc)&(aSq))Sq)"
        );
    }

    @Test
    @DisplayName("Elimination2")
    void elimination2() throws ParseException, IllegalArgumentException {
        separationTest(
                "(a&!(bUc))Sq",
                "((aS(q&!b))&!b&!(bUc))|(!b&!c&(aS(q&!b)))|((!b&!c&q&(aS(q&!b)))Sq)"
        );
    }

    @Test
    @DisplayName("Elimination3")
    void elimination3() throws ParseException, IllegalArgumentException {
        separationTest(
                "aS(q|(bUc))",
                "(aSfalse)|((b|(c&(bUc)))&(aS(!a&!(((!q&!a)S(!a&!b))&!b&!c))))|" +
                        "(!(b|(c&(bUc)))&(aS(!a&!(((!q&!a)S(!a&!b))&!b&!c)))&((!q&!a)S(!a&!b)))"

        );
    }

    @Test
    @DisplayName("Elimination4.V1")
    void elimination4_1() throws ParseException, IllegalArgumentException {
        separationTest(
                "aS(q|!(bUc))",
                "!((!a&!q&(bUc))S!a)&(aStrue)"

        );
    }

    //@Test
    //@DisplayName("Elimination4.V2")
    void elimination4_2() throws ParseException, IllegalArgumentException {
        separationTest(
                "aS(q|!(bUc))",
                "(aS((!a&((!q&!a)S(!a&c)))->!b))"
                + "|(((!q&!a)S(!a&c))->!(b|(c&(bUc))))"
        );
    }

    @Test
    @DisplayName("Elimination5")
    void elimination5() throws ParseException, IllegalArgumentException {
        separationTest(
                "(a&(bUc))S(q|(bUc))",
                "((aSc)&(b|(c&(bUc))))" +
                        "|(((b&(aSc))S!(!b&!c&(!qS!b)))&(b|(c&(bUc))))" +
                        "|(((b&(aSc))S!(!b&!c&(!qS!b)))&!(b|(c&(bUc)))&(!qS!b))"
        );
    }

    @Test
    @DisplayName("Elimination6")
    void elimination6() throws ParseException, IllegalArgumentException {
        separationTest(
                "(a&!(bUc))S(q|(bUc))",
                "((aS(q&!b))&!b&!c)" +
                        "|((!b&!c&(aS(q&!b))&(q|(bUc)))S(q|(bUc)))" +
                        "|((aS(q&!b))&!b&!(bUc))"
        );
    }

    @Test
    @DisplayName("Elimination7")
    void elimination7() throws ParseException, IllegalArgumentException {
        separationTest(
                "(a&(bUc))S(q|!(bUc))",
                "((b&(q|!(bUc))&(aS(q&c)))S(q|!(bUc)))" +
                        "|((aS(q&c))&b)" + "|((aS(q&c))&c&(bUc))"
        );
    }

    @Test
    @DisplayName("Elimination8")
    void elimination8() throws ParseException, IllegalArgumentException {
        separationTest(
                "(a&!(bUc))S(q|!(bUc))",
                "!(!(!a|(bUc))Strue)"+"|((!q&(bUc)&!a)S(!a|(bUc)))"+
                        "|((!q&(bUc))S(!a|(bUc)))"
        );
    }

    private void separationTest(String formula, String expectedFormula) throws ParseException, IllegalArgumentException {
        byte[] formulaBytes = formula.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        System.setIn(formulaStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        BinaryFormula treeFormula = (BinaryFormula) tree.fromSimpleNodeToFormula();
        Formula separatedFormula = separator.applyElimination(treeFormula);
        assertEquals(expectedFormula, separatedFormula.toString());
    }


}