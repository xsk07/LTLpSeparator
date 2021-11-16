package main;

import converter.FormulaConverter;
import formula.Formula;
import graphviz.GraphViz;
import org.apache.commons.cli.*;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.FormulaSeparator;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import static formula.Formula.parseTreeToFormula;
import static params.InputManager.readFile;
import static params.OptionsManager.initializeOptions;
import static params.OutputManager.graphVizOutput;

public class Main {

    private static final int DEFAULT_PROMPT_WIDTH = 160;
    private static final String DEFAULT_ENCODING = "png";
    private static final InputStream DEFAULT_INPUT_SOURCE = System.in;
    private static final String DEFAULT_OUTPUT_FILENAME = "dout/out.";
    private static final Parser parser = new Parser(DEFAULT_INPUT_SOURCE);
    private static final FormulaConverter converter = new FormulaConverter();
    private static final FormulaSeparator separator = new FormulaSeparator();

    public static void main(String[] args) throws ParseException, IllegalArgumentException {

        String header = "Separates a LTLp formula into triples of pure past, pure present and pure future automatons\n\n";
        String footer = "\nPlease report issues at: "; //https://github.com/xsk07/Thesis

        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.setWidth(DEFAULT_PROMPT_WIDTH);

        Options options = initializeOptions();

        formatter.printHelp( "LTLpSepartor", header, options, footer, true);

        CommandLineParser cliparser = new DefaultParser();

        InputStream inputSource = DEFAULT_INPUT_SOURCE;
        String outFile = DEFAULT_OUTPUT_FILENAME;
        String outputEncoding = DEFAULT_ENCODING;

        try {

            CommandLine cmd = cliparser.parse(options, args);
            if(cmd.hasOption("h")) formatter.printHelp( "LTLpSepartor", header, options, footer, true);
            if(cmd.hasOption("iF")) inputSource = readFile(cmd.getOptionValue("iF"));
            if(cmd.hasOption("oF") && cmd.getOptionValue("oF").length() != 0) {
                outFile = "dout/" + cmd.getOptionValue("oF") + ".";
            }
            if(cmd.hasOption("oE")){
                outputEncoding = cmd.getOptionValue("oE");
            }
            if(cmd.hasOption("t")){
                parser.ReInit(inputSource);
                SimpleNode parseTree = parser.Input();
                Formula phi = parseTreeToFormula(parseTree);
                GraphViz gv = phi.fromFormulaToGraphViz();
                graphVizOutput(gv, outFile, outputEncoding);
            }
            if(cmd.hasOption("s")) {
                parser.ReInit(inputSource);
                SimpleNode parseTree = parser.Input();
                Formula phi = parseTreeToFormula(parseTree);
                Formula phic = converter.convert(phi);
                System.out.println("Formula separation: ");
                separator.separate(phic);
                System.out.println("Separation performed.");
                System.out.println("Normalization of the formula: ");
                Formula phin = separator.normalize();
                System.out.println("Normalization performed.");
                System.out.println("Matrix generation");
                //PureFormulaeMatrix m = getPureFormulaeMatrix(phis);
                //matrixToJsonFile(m);
                //System.out.println("Matrix generated.");
                GraphViz gv = phin.fromFormulaToGraphViz();
                graphVizOutput(gv, outFile, outputEncoding);
            }
            if(cmd.hasOption("c")) {
                parser.ReInit(inputSource);
                SimpleNode parseTree = parser.Input();
                Formula phi = parseTreeToFormula(parseTree);
                Formula phic = converter.convert(phi);
                GraphViz gv = phic.fromFormulaToGraphViz();
                graphVizOutput(gv, outFile, outputEncoding);
            }
        } catch (org.apache.commons.cli.ParseException | IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
