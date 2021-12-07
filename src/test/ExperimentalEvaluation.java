package test;

import converter.FormulaConverter;
import formula.Formula;
import graphviz.GraphViz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import separator.FormulaNormalizer;
import separator.FormulaSeparator;
import separator.PureFormulaeMatrix;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import static formula.Formula.parseTreeToFormula;
import static params.OutputManager.*;

public class ExperimentalEvaluation {

    private final Parser parser = new Parser(System.in);
    private static final FormulaConverter converter = new FormulaConverter();
    private static final FormulaSeparator separator = new FormulaSeparator();
    private static final FormulaNormalizer normalizer = new FormulaNormalizer();

    @Test
    @DisplayName("BidirectionalTimeConsequent")
    void bidirectionalTimeConsequent() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("BidirectionalTimeConsequent", parseFormula("G(a -> Fb & Oc)"));
    }

    @Test
    @DisplayName("RespondedExistence")
    void respondedExistence() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("RespondedExistence", parseFormula("G(a -> Fb | Ob)"));
    }

    @Test
    @DisplayName("Precedence")
    void precedence() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("Precedence", parseFormula("G(b -> Oa)"));
    }

    @Test
    @DisplayName("AlternatePrecedence")
    void alternatePrecedence() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("AlternatePrecedence", parseFormula("G(b -> Y(!b S a))"));
    }

    @Test
    @DisplayName("ChainPrecedence")
    void chainPrecedence() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("ChainPrecedence", parseFormula("G(b -> Ya)"));
    }

    @Test
    @DisplayName("Succession")
    void succession() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("Succession", parseFormula("G( (a|b) -> ((a & Fb) | (b & Oa)))"));
    }

    @Test
    @DisplayName("AlternateSuccession")
    void alternateSuccession() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("AlternateSuccession", parseFormula("G( (a|b) -> (a & X(!a U b)) | (b & Y(!b S a)) )"));
    }

    @Test
    @DisplayName("ChainSuccession")
    void chainSuccession() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("ChainSuccession", parseFormula("G((a|b) -> (a & Xb) | (b & Ya))"));
    }

    @Test
    @DisplayName("CoExistence")
    void coExistence() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("CoExistence", parseFormula("G((a|b) -> (a & Fb) | (a & Ob) | (b & Fa) | (b & Oa))"));
    }

    @Test
    @DisplayName("NotCoExistence")
    void notCoExistence() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("NotCoExistence", parseFormula("G((a|b) -> (a & !Ob & !Fb) | (b & !Oa & !Fa))"));
    }

    @Test
    @DisplayName("NonStandard")
    void nonStandard() throws ParseException, ExecutionException, InterruptedException, IOException {
        evaluate("NonStandard", parseFormula("G((Ob & Fe) -> (!c | Ff))"));
    }

    private void evaluate(String filename, Formula phi) throws ExecutionException, InterruptedException, IOException {
        Formula phic = converter.convert(phi);
        separator.separate(phic);
        Formula phis = separator.getRoot();
        Formula phin = normalizer.normalize(phis);
        PureFormulaeMatrix m = normalizer.getPureFormulaeMatrix(phis);
        matrixToJsonFile(m);
        GraphViz gv = phin.fromFormulaToGraphViz();
        //graphVizOutput(gv, "bidirectionalTimeConsequent.", "png");
        //textOutput(filename, phin);
    }


    private Formula parseFormula(String str) throws ParseException {
        byte[] formulaBytes = str.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        parser.ReInit(formulaStream);
        SimpleNode parseTree = parser.Input();
        return parseTreeToFormula(parseTree);
    }


}
