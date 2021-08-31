package main;

import formula.Formula;
import graphviz.GraphViz;
import translator.Translator;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.File;
import static converter.FormulaConverter.convert;
import static separator.FormulaSeparator.separate;
import static translator.Translator.fromFormulaToGraphViz;


public class Main {
    public static void main(String[] args) throws ParseException, IllegalArgumentException {
        System.out.println("Insert an LTL formula: ");
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        Formula phi = Translator.fromSimpleNodeToFormula(tree);
        System.out.println("Before: " + phi.toString());
        Formula phic = convert(phi);
        Formula phie = eliminate(phic);
        System.out.println("After: " + phis.toString());
        GraphViz gv = fromFormulaToGraphViz(phie);

        // begin GraphVizAPI instructions
        gv.increaseDpi();   // 106 dpi
        String type = "gif";
        String repesentationType= "dot";
        File out = new File("PATH/out." + type);
        gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, repesentationType), out );
        // end GraphVizAPI instructions


    }





}
