package main;

import formula.Formula;
import translator.Translator;
import parser.ParseException;
import parser.Parser;
import parser.SimpleNode;
import static converter.FormulaConverter.convert;
import static separator.FormulaSeparator.separate;


public class Main {
    public static void main(String[] args) throws ParseException, IllegalArgumentException {
        System.out.println("Insert an LTL formula: ");
        Parser parser = new Parser(System.in);
        SimpleNode tree = parser.Input();
        Formula phi = Translator.fromSimpleNodeToFormula(tree);
        System.out.println("Before: " + phi.toString());
        Formula phic = convert(phi);
        Formula phis = separate(phic);
        System.out.println("After: " + phis.toString());

    }
}
