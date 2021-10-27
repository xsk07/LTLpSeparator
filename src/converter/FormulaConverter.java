package converter;

import formula.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import static formula.BooleanRules.*;
import static converter.ConversionRules.*;


public class FormulaConverter {

    private Formula root;

    public void setRoot(Formula f) { root = f; }

    public Formula getRoot() { return root; }

    public void updateRoot(Formula f) {
        if(f.getParent() == null && f != root) setRoot(f);
    }

    public Formula convert(Formula phi) throws IllegalArgumentException {

        this.setRoot(phi);

        /* the stack and the queue will contain by construction only OperatorFormulas */
        Stack<Formula> stack = new Stack<>();
        Queue<Formula> queue = new LinkedList<>();
        /* if phi is an OperatorFormula then initialize the queue with it */
        if(phi.isOperator()) queue.add(phi);

        while(!queue.isEmpty()) {
            OperatorFormula f = (OperatorFormula) queue.remove();
            if(f.getOperator().isDerived()) stack.push(f);
            if(f.isUnary()) {
                UnaryFormula uf = (UnaryFormula) f;
                if(uf.getOperand().isOperator()) queue.add(uf.getOperand());
            }
            if(f.isBinary()) {
                BinaryFormula bf = (BinaryFormula) f;
                if(bf.getLoperand().isOperator()) queue.add(bf.getLoperand());
                if(bf.getRoperand().isOperator()) queue.add(bf.getRoperand());
            }
        }

        while(!stack.isEmpty()) {
            OperatorFormula f = (OperatorFormula) stack.pop();
            if(f.isUnary()) updateRoot(
                    f.replaceFormula(
                            applyUnaryRule((UnaryFormula) f)
                    )
            );
            else if(f.isBinary()) updateRoot(
                    f.replaceFormula(
                            applyBinaryRule((BinaryFormula) f)
                    )
            );
        }

        return root;
    }

    /** Returns the formula which is the result of the application of
     * the unary rule corresponding to the operator of the formula got in input.
     * @return Returns the formula which is the result of the application of
     * the unary rule corresponding to the operator of the formula got in input
     * @param f An UnaryFormula */
    private Formula applyUnaryRule(UnaryFormula f) throws IllegalArgumentException {
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
    private BinaryFormula applyBinaryRule(BinaryFormula f) {
        return switch (f.getOperator()) {
            case IMPL -> implicationRule(f);
            case EQUIV -> equivalenceRule(f);
            case UNLESS -> ruleW(f);
            default -> f;
        };
    }

}