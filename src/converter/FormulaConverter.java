package converter;

import formula.*;

import java.util.LinkedList;
import java.util.Queue;

import static formula.BooleanRules.*;
import static converter.ConversionRules.*;


public class FormulaConverter {

    /** Converts a formula, applying a set of equivalence and rewrite rules, into a form
     * containing only boolean and the two binary temporal operators since and until.
     * @param f The formula to be converted
     * @return Returns a formula containing only boolean and the binary operators since
     * and until */
    public static Formula convert(Formula f) throws IllegalArgumentException {
        if(f.isOperator()){
            OperatorFormula of = (OperatorFormula) f;
            if(of.isUnary()) {
                UnaryFormula uf = (UnaryFormula) f;
                uf.setOperand(convert(uf.getOperand()));
                return applyUnaryRule(uf);
            }
            if(of.isBinary()){
                BinaryFormula bf = (BinaryFormula) f;
                bf.setLoperand(convert(bf.getLoperand()));
                bf.setRoperand(convert(bf.getRoperand()));
                return applyBinaryRule(bf);
            }
        }
        return f;
    }

    /** Returns the formula which is the result of the application of
     * the unary rule corresponding to the operator of the formula got in input.
     * @return Returns the formula which is the result of the application of
     * the unary rule corresponding to the operator of the formula got in input
     * @param f An UnaryFormula */
    public static Formula applyUnaryRule(UnaryFormula f) throws IllegalArgumentException {
        return switch (f.getOperator()) {
            case NOT -> involution(f);
            case ONCE -> ruleO(f);
            case HIST -> ruleH(f);
            case YEST -> ruleY(f);
            case FIN -> ruleF(f);
            case GLOB -> ruleG(f);
            case NEXT -> ruleX(f);
            default -> f;
        };
    }

    /** Returns the formula which is the result of the application of
     * the binary rule corresponding to the operator of the formula got in input.
     * @return Returns the formula which is the result of the application of
     * the binary rule corresponding to the operator of the formula got in input
     * @param f An UnaryFormula */
    public static BinaryFormula applyBinaryRule(BinaryFormula f) {
        return switch (f.getOperator()) {
            case IMPL -> implicationRule(f);
            case EQUIV -> equivalenceRule(f);
            case UNLESS -> ruleW(f);
            default -> f;
        };
    }



}
