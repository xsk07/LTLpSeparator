package simplifier;

import formula.*;
import java.util.ArrayList;
import java.util.List;
import static formula.Operator.OR;
import static formula.Operator.AND;
import static formula.AtomConstant.*;
import static formula.Formula.opposite;
import static formula.BinaryFormula.newConjunction;
import static formula.BinaryFormula.newDisjunction;

public class FormulaSimplifier {

    /** Removes redundancies and oppositions from the operands of a combination of operators
     *  of the same type.
     *  @return returns the simplified formula if f had one of '&' or '|' as operator
     *  and returns f itself otherwise
     *  @param f A BinaryFormula whose operator must be one of '&' or '|' */
    public static Formula simplify(BinaryFormula f) {
        ArrayList<Formula> newOperands = getNewOperands(f);
        if(f.isOperator(OR)) return newDisjunction(newOperands);
        if (f.isOperator(AND)) return newConjunction(newOperands);
        return f;
    }

    private static ArrayList<Formula> getNewOperands(BinaryFormula f) {
        /* The truthConstant represent the truth value to use to create a new
         * AtomicFormula to combine with the other in presence of two or more
         * opposite formulae.
         * If the operator of f is AND then set the truthConstant to FALSE
         * else if the operator of f is OR then set the truthConstant to TRUE
         * In fact: let x be a wff, for any x assignment:
         * x & !x == false
         * x | !x  == true
         */
        AtomConstant truthConstant = f.getOperator().equals(OR) ? TRUE : FALSE;
        ArrayList<Formula> operands = f.getCombinationOperands();
        ArrayList<Formula> newOperands = new ArrayList<>();
        // list of the already checked formulae
        ArrayList<Formula> checked = new ArrayList<>();
        operands.forEach(x -> {
            // if the newOperands list is empty then initialise it with x
            if(newOperands.isEmpty()) {
                newOperands.add(x);
                checked.add(x);
            }
            else {
                // if a such formula has never been checked then
                boolean neverChecked = checked.stream().noneMatch(z -> z.equalTo(x));
                if(neverChecked) {
                    // opposite formula to x
                    List<Formula> oppositeFormulae = newOperands
                            .stream()
                            .filter(z -> opposite(z, x)).toList();
                    /* if the opposite formula is not null and the truth value is not yet a new operand then
                     * remove the opposite formula from the list of new operands and add the truth value atom */

                    if(!oppositeFormulae.isEmpty()) {
                        // it is true if, and only if, the truth value is already a new operand
                        boolean notContainsTruthAtom = newOperands.stream().noneMatch(
                                z -> z instanceof AtomicFormula az && az.isTruthValue(truthConstant)
                        );
                        oppositeFormulae.forEach(newOperands::remove);
                        if(notContainsTruthAtom) newOperands.add(new AtomicFormula(truthConstant));
                    }
                    else newOperands.add(x);
                    checked.add(x);
                }
            }
        });
        return newOperands;
    }

}
