package test;

import formula.*;
import graphviz.GraphViz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.JunctionPath;
import java.io.ByteArrayInputStream;
import static params.OutputManager.graphVizOutput;
import static separator.FormulaSeparator.getNextOperator;

class JunctionPathTest {

    @Test
    @DisplayName("Prova0")
    void prova0() throws ParseException  {
        pathTest("q S ( (a & (b & (cUd))) | p)");
    }

    @Test
    @DisplayName("Prova1")
    void prova1() throws ParseException  {
        pathTest("q S (a | ((b & ((c | d) | e | f & !(xUy)) & g ) & h) & z)");
    }

    @Test
    @DisplayName("Prova2")
    void prova2() throws ParseException  {
        pathTest("(a & ((b | ((c & d) & e & f & !(xUy)) | g ) | h) | z) S q");
    }


    private void pathTest(String sf) throws ParseException {
        byte[] formulaBytes = sf.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        System.setIn(formulaStream);
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        BinaryFormula f = (BinaryFormula) tree.fromSimpleNodeToFormula();
        BinaryFormula x = f;
        BinaryFormula y = (BinaryFormula) getNextOperator(x, x.getOperator().getMirrorOperator());
        JunctionPath jp_xy = new JunctionPath(x, y);
        BinaryFormula r = jp_xy.setupJunctionPath();
        GraphViz gv = r.fromFormulaToGraphViz();
        graphVizOutput(gv, "dout/out.", "png");
    }






}