package main;

import converter.FormulaConverter;
import formula.Formula;
import graphviz.GraphViz;
import org.apache.commons.cli.*;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.FormulaNormalizer;
import separator.FormulaSeparator;
import separator.PureFormulaeMatrix;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import static formula.Formula.parseTreeToFormula;
import static params.InputManager.readFile;
import static params.OptionsManager.initializeOptions;
import static params.OutputManager.*;

public class Main {

    private static final int DEFAULT_PROMPT_WIDTH = 160;
    private static final String DEFAULT_ENCODING = "png";
    private static final InputStream DEFAULT_INPUT_SOURCE = System.in;
    private static final String DEFAULT_OUTPUT_FILENAME = "out.";
    private static final Parser parser = new Parser(DEFAULT_INPUT_SOURCE);
    private static final FormulaConverter converter = new FormulaConverter();
    private static final FormulaSeparator separator = new FormulaSeparator();
    private static final FormulaNormalizer normalizer = new FormulaNormalizer();

    public static void main(String[] args) throws ParseException, IllegalArgumentException {

        String header = "Separates an LTLp formula into a combination of pure formulae and generates the corresponding separated automata set \n\n";
        String footer = "\nPlease report issues at:  https://github.com/xsk07/LTLpSeparator";

        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.setWidth(DEFAULT_PROMPT_WIDTH);

        Options options = initializeOptions();

        formatter.printHelp( "LTLpSeparator", header, options, footer, true);

        CommandLineParser cliparser = new DefaultParser();

        InputStream inputSource = DEFAULT_INPUT_SOURCE;
        String outFile = DEFAULT_OUTPUT_FILENAME;
        String outputEncoding = DEFAULT_ENCODING;

        try {

            CommandLine cmd = cliparser.parse(options, args);
            if(cmd.hasOption("h")) formatter.printHelp( "LTLpSepartor", header, options, footer, true);
            if(cmd.hasOption("iF")) inputSource = readFile(cmd.getOptionValue("iF"));
            if(cmd.hasOption("oF") && cmd.getOptionValue("oF").length() != 0) {
                outFile = cmd.getOptionValue("oF") + ".";
            }
            if(cmd.hasOption("oE")){
                outputEncoding = cmd.getOptionValue("oE");
            }
            if(cmd.hasOption("t")){
                Formula result = parseFormula(inputSource);
                outputTask(result, outFile, outputEncoding);
            }
            if(cmd.hasOption("c")) {
                Formula result = performConversion(parseFormula(inputSource));
                outputTask(result, outFile, outputEncoding);
            }
            if(cmd.hasOption("s")) {
                Formula result = performSeparation(
                        performConversion(
                                parseFormula(inputSource)
                        )
                );
                //result = converter.backConversion(result);
                outputTask(result, outFile, outputEncoding);
            }
            if(cmd.hasOption("a")) {
                Formula result = performNormalization(
                        performSeparation(
                                performConversion(
                                        parseFormula(inputSource)
                                )
                        )
                );
                PureFormulaeMatrix m = normalizer.getPureFormulaeMatrix(result);
                matrixToJsonFile(m);
                //result = converter.backConversion(result);
                outputTask(result, outFile, outputEncoding);
            }
        } catch (org.apache.commons.cli.ParseException | IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static Formula parseFormula(InputStream inputSource) throws ParseException {
        parser.ReInit(inputSource);
        SimpleNode parseTree = parser.Input();
        Formula phi = parseTreeToFormula(parseTree);
        return phi;
    }

    private static Formula performConversion(Formula phi) {
        System.out.println("Formula conversion.");
        Formula phic = converter.convert(phi);
        System.out.println("Conversion performed.");
        return phic;
    }

    private static Formula performSeparation(Formula phi) throws ExecutionException, InterruptedException {
        System.out.println("Formula separation, applied rules: ");
        Formula phis = separator.separate(phi);
        System.out.println("Separation performed.");
        return phis;
    }

    private static Formula performNormalization(Formula phi) {
        System.out.println("Normalization.");
        Formula phin = normalizer.normalize(phi);
        System.out.println("Normalization performed.");
        return phin;
    }

    private static void outputTask(Formula result, String file, String encoding) throws IOException {
        if(Objects.equals(encoding, "txt")) textOutput(file, result);
        else {
            GraphViz gv = result.fromFormulaToGraphViz();
            graphVizOutput(gv, file, encoding);
        }
    }

}
