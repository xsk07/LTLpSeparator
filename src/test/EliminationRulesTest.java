package test;

import converter.FormulaConverter;
import formula.Formula;
import graphviz.GraphViz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import static formula.BinaryFormula.newConjunction;
import static formula.BinaryFormula.newDisjunction;
import static formula.Formula.parseTreeToFormula;
import static formula.Operator.SINCE;
import static formula.Operator.UNTIL;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static params.OutputManager.graphVizOutput;
import static separator.FormulaEliminator.EliminationRules.*;

class EliminationRulesTest {

    private final Parser parser = new Parser(System.in);
    private final FormulaConverter converter = new FormulaConverter();

    /* ELIMINATION 1
     * S(a & U(A,B), q) =>* E = E1 | E2 | E3, where:
     * E1 = S(a,q) & S(a,B) & B & U(A,B)
     * E2 = A & S(a, B & q)
     * E3 = S(A & q & S(a,B) & S(a,q), q) */
    @Test
    @DisplayName("Elimination1")
    void elimination1Test() throws ParseException {
        // SINCE
        Formula expectedE1 = parseFormula("qSa & cSa & c & cUb");
        Formula expectedE2 = parseFormula("b & (c & q)Sa");
        Formula expectedE3 = parseFormula("qS(b & q & cSa & qSa)");
        Formula expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        Formula e1 = elimination1(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e1);
        GraphViz gv = e1.fromFormulaToGraphViz();
        graphVizOutput(gv, "dout/eliminationTest.", "png");
        // UNTIL
        expectedE1 = parseFormula("qUa & cUa & c & cSb");
        expectedE2 = parseFormula("b & (c & q)Ua");
        expectedE3 = parseFormula("qU(b & q & cUa & qUa)");
        expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        e1 = elimination1(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e1);
    }

    /* S(a, q | U(A,B)) =>* E = E1 | E2 | E3, where:
     * E1 = S(a, false)
     * E2 = (A | (B & U(A,B))) & S(a, !a & !c)
     * E3 = !(A | (B & U(A,B))) & S(a, !a & !c) & S(!q & !a, !a & !A)
     * c = S(!q & !a, !a & !A) & !A & !B
     * */
    @Test
    @DisplayName("Elimination2")
    void elimination2Test() throws ParseException {
        // SINCE
        Formula expectedE1 = parseFormula("falseSa");
        Formula expectedE2 = parseFormula("(b | (c & cUb)) & (!a & !((!a & !b) S (!q & !a) & !b & !c)) S a");
        Formula expectedE3 = parseFormula("!(b | (c & cUb)) & (!a & !((!a & !b)S(!q & !a)  & !b & !c))Sa & (!a & !b)S(!q & !a)");
        Formula expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        Formula e2 = elimination2(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e2);
        // UNTIL
        expectedE1 = parseFormula("falseUa");
        expectedE2 = parseFormula("(b | (c & cSb)) & (!a & !((!a & !b) U (!q & !a) & !b & !c)) U a");
        expectedE3 = parseFormula("!(b | (c & cSb)) & (!a & !((!a & !b)U(!q & !a)  & !b & !c))Ua & (!a & !b)U(!q & !a)");
        expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        e2 = elimination2(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e2);
    }


    /* ELIMINATION 3
     * S(a & !U(A,B), q) =>* E = E1 | E2 | E3, where:
     * E1 = S(a, q & !A) & !A & !U(A,B)
     * E2 = !A & !B & S(a, !A & q)
     * E3 = S(!A & !B & q & S(a, !A & q), q) */
    @Test
    @DisplayName("Elimination3")
    void elimination3Test() throws ParseException {
        // SINCE
        Formula expectedE1 = parseFormula("(q & !b)Sa & !b & !(cUb)");
        Formula expectedE2 = parseFormula("!b & !c & (!b & q)Sa ");
        Formula expectedE3 = parseFormula("qS(!b & !c & q & (!b & q)Sa)");
        Formula expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        Formula e3 = elimination3(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e3);
        // UNTIL
        expectedE1 = parseFormula("(q & !b)Ua & !b & !(cSb)");
        expectedE2 = parseFormula("!b & !c & (!b & q)Ua ");
        expectedE3 = parseFormula("qU(!b & !c & q & (!b & q)Ua)");
        expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        e3 = elimination3(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e3);
    }

    /* ELIMINATION 4
     * S(a, q | !U(A,B)) =>* !S(!q & U(A,B) & !a, !a) & Oa */
    @Test
    @DisplayName("Elimination4")
    void elimination4Test() throws ParseException {
        // SINCE
        Formula expected = newConjunction(Arrays.asList(
                parseFormula("!(!aS(!q & cUb & !a))"),
                converter.convert(parseFormula("Oa "))
                )
        );
        Formula e4 = elimination4(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e4);
        // UNTIL
        expected = newConjunction(Arrays.asList(
                        parseFormula("!(!aU(!q & cSb & !a))"),
                        converter.convert(parseFormula("Fa"))
                )
        );
        e4 = elimination4(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e4);
    }

    /* ELIMINATION 5
     * S(a & U(A,B), q | U(A,B)) =>* E = E1 | E2 | E3, where:
     * E1 = S(a,B) & (A | (B & U(A,B)))
     * E2 = S(A & S(a,B), !b0) & d
     * E3 = S(A & S(a,B), !b0) & !d & !S(!q, !A)
     * b0 = !A & !B & S(!q, !A)
     *  d = A | (B & U(A,B)) */
    @Test
    @DisplayName("Elimination5")
    void elimination5Test() throws ParseException {
        // SINCE
        Formula expectedE1 = parseFormula("cSa & (b | (c & cUb))");
        Formula expectedE2 = parseFormula("!(!b & !c & !bS!q) S(b & cSa) & (b | (c & cUb))");
        Formula expectedE3 = parseFormula("!(!b & !c & (!b)S(!q)) S (b & cSa) & !(b | (c & cUb)) & !((!b)S(!q))");
        Formula expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        Formula e5 = elimination5(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e5);
        // UNTIL
        expectedE1 = parseFormula("cUa & (b | (c & cSb))");
        expectedE2 = parseFormula("!(!b & !c & !bU!q) U (b & cUa) & (b | (c & cSb))");
        expectedE3 = parseFormula("!(!b & !c & (!b)U(!q)) U (b & cUa) & !(b | (c & cSb)) & !((!b)U(!q))");
        expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        e5 = elimination5(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e5);
    }

    /* ELIMINATION 6
    *  S(a & !U(A,B), q | U(A,B)) =>* E = E1 | E2 | E3, where:
    *  E1 = S(a, !A & q) & !A & !B
    *  E2 = S(!A & !B & (q | U(A,B)) & S(a, !A & q), q | U(A,B))
    *  E3 = S(a, q & !A) & !A & !U(A,B) */
    @Test
    @DisplayName("Elimination6")
    void elimination6Test() throws ParseException {
        // SINCE
        Formula expectedE1 = parseFormula("(!b & q)Sa & !b & !c");
        Formula expectedE2 = parseFormula("(q | cUb)S(!b & !c & (q | cUb) & (!b & q)Sa )");
        Formula expectedE3 = parseFormula("(q & !b)Sa & !b & !(cUb) ");
        Formula expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        Formula e6 = elimination6(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e6);
        // UNTIL
        expectedE1 = parseFormula("(!b & q)Ua & !b & !c");
        expectedE2 = parseFormula("(q | cSb)U(!b & !c & (q | cSb) & (!b & q)Ua )");
        expectedE3 = parseFormula("(q & !b)Ua & !b & !(cSb) ");
        expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        e6 = elimination6(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e6);
    }

    /* ELIMINATION 7
     * S(a & U(A,B), q | !U(A,B)) =>* E = E1 | E2 | E3, where:
     * E1 = S(A & (q | !U(A,B)) & S(a, B & q), q | !U(A,B))
     * E2 = S(a, B & q) & A
     * E3 = S(a, B & q) & B & U(A,B) */
    @Test
    @DisplayName("Elimination7")
    void elimination7Test() throws ParseException {
        // SINCE
        Formula expectedE1 = parseFormula("(q | !(cUb))S(b & (q | !(cUb)) & (c & q)Sa)");
        Formula expectedE2 = parseFormula("(c & q)Sa & b");
        Formula expectedE3 = parseFormula("(c & q)Sa & c & cUb");
        Formula expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        Formula e7 = elimination7(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e7);
        // UNTIL
        expectedE1 = parseFormula("(q | !(cSb))U(b & (q | !(cSb)) & (c & q)Ua)");
        expectedE2 = parseFormula("(c & q)Ua & b");
        expectedE3 = parseFormula("(c & q)Ua & c & cSb");
        expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3));
        e7 = elimination7(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e7);
    }

    /* ELIMINATION 8
     * S(a & !U(A,B), q | !U(A,B)) =>* E = !(E1 | E2 | E3), where:
     * E1 = H(!a | U(A,B))
     * E2 = S(!q & U(A,B) & !a, !a | U(A,B))
     * E3 = S(!q & U(A,B), !a | U(A,B)) */
    @Test
    @DisplayName("Elimination8")
    void elimination8Test() throws ParseException {
        // SINCE
        Formula expectedE1 = converter.convert(parseFormula("H(!a | cUb)"));
        Formula expectedE2 = parseFormula("(!a | cUb)S(!q & cUb & !a)");
        Formula expectedE3 = parseFormula("(!a | cUb)S(!q & cUb)");
        Formula expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3)).negate();
        Formula e8 = elimination8(standardFormulaeArray(), SINCE);
        compareFormulae(expected, e8);
        // UNTIL
        expectedE1 = converter.convert(parseFormula("G(!a | cSb)"));
        expectedE2 = parseFormula("(!a | cSb)U(!q & cSb & !a)");
        expectedE3 = parseFormula("(!a | cSb)U(!q & cSb)");
        expected = newDisjunction(Arrays.asList(expectedE1, expectedE2, expectedE3)).negate();
        e8 = elimination8(standardFormulaeArray(), UNTIL);
        compareFormulae(expected, e8);
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