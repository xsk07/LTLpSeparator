package converter;

import formula.*;
import java.util.*;
import static converter.BackConversionRules.*;
import static formula.BooleanRules.*;
import static converter.ConversionRules.*;
import static formula.Operator.*;
import static formula.Operator.YEST;
import static separator.FormulaSimplifier.simplify;

public class FormulaConverter {

    private Formula root;

    public void setRoot(Formula f) { root = f; }

    public Formula getRoot() { return root; }

    public void updateRoot(Formula f) {
        if(f.getParent() == null && f != root) setRoot(f);
    }


    public Formula convert(Formula phi) throws IllegalArgumentException {

        this.setRoot(phi);

        /* the stack and the queue will contain by construction only OperatorFormula's */
        Stack<Formula> stack = new Stack<>();
        Queue<Formula> queue = new LinkedList<>();
        /* if phi is an OperatorFormula then initialize the queue with it */
        if(phi instanceof OperatorFormula) queue.add(phi);

        while(!queue.isEmpty()) {
            OperatorFormula f = (OperatorFormula) queue.remove();
            if(f.getOperator().isDerived()) stack.push(f);
            if(f instanceof UnaryFormula uf) {
                if(uf.getOperand() instanceof OperatorFormula) queue.add(uf.getOperand());
            }
            if(f instanceof BinaryFormula bf) {
                if(bf.getLoperand() instanceof OperatorFormula) queue.add(bf.getLoperand());
                if(bf.getRoperand() instanceof OperatorFormula) queue.add(bf.getRoperand());
            }
        }

        while(!stack.isEmpty()) {
            OperatorFormula f = (OperatorFormula) stack.pop();
            if(f instanceof UnaryFormula uf) updateRoot(
                    f.replaceFormula(applyUnaryRule(uf))
            );
            else if(f instanceof BinaryFormula bf) updateRoot(
                    f.replaceFormula(applyBinaryRule(bf))
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

    public Formula backConversion(Formula phi) {
        this.setRoot(phi);

        /* the stack and the queue will contain by construction only OperatorFormulae */
        Stack<Formula> stack = new Stack<>();
        Queue<Formula> queue = new LinkedList<>();
        /* if phi is an OperatorFormula then initialize the queue with it */
        if(phi instanceof OperatorFormula) queue.add(phi);

        while(!queue.isEmpty()) {
            OperatorFormula f = (OperatorFormula) queue.remove();
            stack.push(f);
            if(f instanceof UnaryFormula uf) {
                if(uf.getOperand() instanceof OperatorFormula) queue.add(uf.getOperand());
            }
            if(f instanceof BinaryFormula bf) {
                if(bf.getLoperand() instanceof OperatorFormula) queue.add(bf.getLoperand());
                if(bf.getRoperand() instanceof OperatorFormula) queue.add(bf.getRoperand());
            }
        }

        while (!stack.isEmpty()) {
            Formula f = stack.pop();

            if(f instanceof UnaryFormula uf) {
                if(uf.isOperator(NOT)) {
                    if(needTruthValueNegation(uf)){
                        Formula nf = truthValueNegation(uf);
                        uf.replaceFormula(nf);
                        updateRoot(nf);
                    }
                    if(needsInvolution(uf)){
                        Formula nf = involution(uf);
                        uf.replaceFormula(nf);
                        updateRoot(nf);
                    }
                }
            }
            else if(f instanceof BinaryFormula bf) {
                if(derivedOperator(bf) != null) {
                    switch(derivedOperator(bf)) {
                        case FIN: {
                            UnaryFormula nf = (UnaryFormula) f.replaceFormula(backF(bf));
                            updateRoot(nf);
                            break;
                        }
                        case GLOB: {
                            OperatorFormula p = f.getParent();
                            updateRoot(p.replaceFormula(backG(bf)));
                            break;
                        }
                        case HIST: {
                            OperatorFormula p = f.getParent();
                            updateRoot(p.replaceFormula(backH(bf)));
                            break;
                        }
                        case ONCE: {
                            updateRoot(f.replaceFormula(backO(bf)));
                            break;
                        }
                        case NEXT: {
                            updateRoot(f.replaceFormula(backX(bf)));
                            break;
                        }
                        case YEST: {
                            updateRoot(f.replaceFormula(backY(bf)));
                            break;
                        }
                        case UNLESS: {
                            updateRoot(f.replaceFormula(backW(bf)));
                            break;
                        }
                        default: break;
                    }
                }
                // perform some simplifications of the formula based on rules of the boolean logic
                else {
                    Formula nf = simplify(bf);
                    bf.replaceFormula(nf);
                    updateRoot(nf);
                }
            }
        }
        return getRoot();
    }

    private static Operator derivedOperator(BinaryFormula f) {
        if(patternG(f)) return GLOB; // !U(!q, true)
        if(patternF(f)) return FIN;  // U(q, true)
        if(patternX(f)) return NEXT; // U(q, false)
        if(patternH(f)) return HIST; // !S(!q, true)
        if(patternO(f)) return ONCE; // S(q, true)
        if(patternY(f)) return YEST; // S(q, false)
        if(patternW(f)) return UNLESS; // U(p,q) | Gq
        return null;
    }

}