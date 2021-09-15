package main;

import formula.Formula;
import graphviz.GraphViz;
import org.apache.commons.cli.*;
import translator.Translator;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.IOException;
import java.io.InputStream;
import static converter.FormulaConverter.convert;
import static params.InputManager.readFile;
import static params.OptionsManager.initializeOptions;
import static params.OutputManager.graphVizOutput;
import static separator.FormulaSeparator.*;
import static translator.Translator.fromFormulaToGraphViz;


public class Main {

    private static final int DEFAULT_PROMPT_WIDTH = 160;
    public static final String DEFAULT_ENCODING = "png";
    public static final InputStream DEFAULT_INPUT_SOURCE = System.in;
    public  static final String DEFAULT_OUTPUT_FILENAME = "dout/out.";


    public static void main(String[] args) throws ParseException, IllegalArgumentException {

        String header = "Separates a LTLf formula into a triple of pure past, pure present and pure future ones\n\n";
        String footer = "\nPlease report issues at https://github.com/xsk07/Thesis";

        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.setWidth(DEFAULT_PROMPT_WIDTH);

        Options options = initializeOptions();

        formatter.printHelp( "LTLfSepartor", header, options, footer, true);

        CommandLineParser cliparser = new DefaultParser();

        InputStream inputSource = DEFAULT_INPUT_SOURCE;
        String outFile = DEFAULT_OUTPUT_FILENAME;
        String outputEncoding = DEFAULT_ENCODING;

        try {

            CommandLine cmd = cliparser.parse(options, args);
            if(cmd.hasOption("h")) formatter.printHelp( "LTLfSepartor", header, options, footer, true);
            if(cmd.hasOption("iF")) inputSource = readFile(cmd.getOptionValue("iF"));
            if(cmd.hasOption("oF") && cmd.getOptionValue("oF").length() != 0) {
                outFile = "dout/" + cmd.getOptionValue("oF") + ".";
            }
            if(cmd.hasOption("oE")){
                outputEncoding = cmd.getOptionValue("oE");
            }
            if(cmd.hasOption("t")){
                Parser parser = new Parser(inputSource);
                SimpleNode tree = parser.Input();
                Formula phi = Translator.fromSimpleNodeToFormula(tree);
                GraphViz gv = fromFormulaToGraphViz(phi);
                graphVizOutput(gv, outFile, outputEncoding);
            }
            if(cmd.hasOption("s")) {
                Parser parser = new Parser(inputSource);
                SimpleNode tree = parser.Input();
                Formula phi = Translator.fromSimpleNodeToFormula(tree);
                Formula phic = convert(phi);
                Formula phie = separate(phic);
                GraphViz gv = fromFormulaToGraphViz(phie);
                graphVizOutput(gv, outFile, outputEncoding);
            }
            if(cmd.hasOption("c")) {
                Parser parser = new Parser(inputSource);
                SimpleNode tree = parser.Input();
                Formula phi = Translator.fromSimpleNodeToFormula(tree);
                Formula phic = convert(phi);
                GraphViz gv = fromFormulaToGraphViz(phic);
                graphVizOutput(gv, outFile, outputEncoding);
            }
        } catch (org.apache.commons.cli.ParseException | IOException e) {
            e.printStackTrace();
        }

    }

}
