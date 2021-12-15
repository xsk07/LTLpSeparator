package test;

import formula.Formula;
import formula.Junction;
import formula.Path;
import org.junit.jupiter.api.Test;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import static formula.Formula.parseTreeToFormula;
import static separator.FormulaSeparator.maxKofDegreeM;

class DegreeTest {

    private final Parser parser = new Parser(System.in);

    @Test
    void degreeTest() throws ParseException {
        Formula f = parseFormula("xS(((!r|((eUd)S(p))U(zU(yUx)))S(!(qUp)))Uk)");
        ArrayList<Path> paths = f.getPaths();
        int m = f.degree();
        System.out.println("Degree: " + m);
        Junction topJunction = paths.get(1).getTopJunction();
        System.out.println("Top junction: " + topJunction);
        System.out.println("Top junction k: " + topJunction.getK());
        System.out.println("Max k of productions of getDegree m: "  +  maxKofDegreeM(f, m));
    }

    private Formula parseFormula(String str) throws ParseException {
        byte[] formulaBytes = str.getBytes();
        ByteArrayInputStream formulaStream = new ByteArrayInputStream(formulaBytes);
        parser.ReInit(formulaStream);
        SimpleNode parseTree = parser.Input();
        return parseTreeToFormula(parseTree);
    }


}