package test;

import formula.Formula;
import formula.TimeConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;
import static formula.TimeConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FormulaTimeTest {

    @Test
    @DisplayName("Participation")
    void participationTime() throws ParseException, IllegalArgumentException {
        formulaTime("Fa", FUTURE);
    }

    @Test
    @DisplayName("Prova0")
    void prova0() throws ParseException, IllegalArgumentException {
        formulaTime("q S ( (a & (b & (cUd))) | p)", MIXED);
    }

    @Test
    @DisplayName("Prova1")
    void prova1() throws ParseException, IllegalArgumentException {
        formulaTime("q S (a | ((b & ((c | d) | e | f & !(xUy)) & g ) & h) & z)", MIXED);
    }

    @Test
    @DisplayName("Prova2")
    void prova2() throws ParseException, IllegalArgumentException {
        formulaTime("(a & ((b | ((c & d) & e & f & !(xUy)) | g ) | h) | z) S q", MIXED);
    }

    @Test
    @DisplayName("Prova3")
    void prova3() throws ParseException, IllegalArgumentException {
        formulaTime("(b|((c&d&e&f)&!(xUy)))|g", MIXED);
    }



    private void formulaTime(String f, TimeConstant t) throws ParseException {
        byte[] templateBytes = f.getBytes();
        ByteArrayInputStream templateStream = new ByteArrayInputStream(templateBytes);
        System.setIn(templateStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        Formula templateFormula = tree.fromSimpleNodeToFormula();
        templateFormula.debugTimePrint();
        assertEquals(t, templateFormula.getTime());
    }





}