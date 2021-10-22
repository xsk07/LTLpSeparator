package separator;

import formula.*;
import static formula.Operator.*;
import static separator.FormulaSeparator.searchX;
import static separator.Lemmas.*;
import static formula.BooleanRules.*;
import static formula.AtomConstant.*;
import static separator.FormulaSeparator.getNextOperator;
import static separator.OperatorChain.rearrangeInnerFormula;

public class JunctionPath {

    private BinaryFormula x, y;
    private Operator op;

    public JunctionPath(BinaryFormula fx, BinaryFormula fy) {

        if(!fx.isAncestorOf(fy)) throw new IllegalArgumentException(
                "fx has to be an ancestor of fy"
        );

        x = fx; y = fy;

        /* if y is nested inside the left subtree of x then
        * set the operator of the junction to AND */
        if(y.isInLeftSubtreeOf(x)) op = AND;

        /* if y is nested inside the left subtree of x then
        * set the operator of the junction to OR */
        if(y.isInRightSubtreeOf(x)) op = OR;

    }

    public void setX(BinaryFormula f) { x = f; }

    public void setY(BinaryFormula f) { y = f; }

    public BinaryFormula getX() { return x; }

    public BinaryFormula getY() { return y; }

    /** @return Returns the ruling operator of the junction path */
    public Operator getOperator() { return op; }

    /** @return Returns true if, and only if, the junction path is inside the left
     * subtree of the root node of the junction */
    public boolean isLeft(){ return op.equals(AND); }

    /** @return Returns true if, and only if, the junction path is inside the right
     * subtree of the root node of the junction */
    public boolean isRight(){ return op.equals(OR); }

    /** Prints the string representation of the junction path. */
    public void printPath() {
        OperatorFormula z = y;
        StringBuilder s = new StringBuilder(z.getOperator().getImage());
        while (z != x) {
            s.append(",");
            z = z.getParent();
            s.append(z.getOperator().getImage());
        }
        String revS = new StringBuilder(s.toString()).reverse().toString();
        System.out.println("< " + revS + " >");
    }

    /** @return Returns true if, and only if, the junction path is arranged,
     * it means that no rule could be applied on the nodes of the junction path */
    public boolean isArranged() {
        OperatorFormula z = x;
        while(z != y) {
            if(z.isOperator(NOT)) {
                assert z instanceof UnaryFormula;
                UnaryFormula uz = (UnaryFormula) z;
                if(needsInvolution(uz)) return false;
                if(needsDeMorganLaw(uz)) return false;
                z = (OperatorFormula) uz.getOperand();
            }
            // if z is a binary formula then
            if(z.isBinary()) {
                BinaryFormula bz = (BinaryFormula) z;
                /* if the junction is in the left branch of the formula
                 * and z has AND as operator */
                if(this.isLeft() && z.isOperator(AND)) {
                    /* if z needs the application of the distributive law then
                     * return false */
                    if(needsDistributiveLaw(bz)) return false;
                }

                /* if the junction is in the right branch of the formula
                 * and z has OR as operator */
                if(this.isRight() && z.isOperator(OR)) {
                    /* if z needs the application of the distributive law then
                     * return false */
                    if(needsDistributiveLaw(bz)) return false;
                }
                /* if y is in the left subtree of z then update z to its left child
                 * if y is in the right subtree of z then update z to its right child */
                if(y.isInLeftSubtreeOf(bz)) z = (OperatorFormula) bz.getLoperand();
                if(y.isInRightSubtreeOf(bz)) z = (OperatorFormula) bz.getRoperand();
            }
        }
        return true;
    }


    /** @return returns true if, and only if, the junction path needs to be arranged */
    public boolean needsArrangement(){ return !isArranged(); }

    /** Arranges the junction path. */
    public void arrange() {
        while(this.needsArrangement()) {
            OperatorFormula z = x;
            while (z != y) z = this.applyArrangement(z);
        }
    }

    /** Applies on the node z the applicable arrangement rule.
     * @return the formula obtain from the application of the arrangement
     * @param z the formula on which to be applied the arrangement rule */
    private OperatorFormula applyArrangement(OperatorFormula z) {

        /* if the operator of node z is a NOT then
         * if it's needed to apply the involution rule then
         * apply it and return its result
         * if it's needed to apply the De Morgan law then
         * apply it and return its result
         * if no rule is needed return its operand child */
        if(z.isOperator(NOT)) {
            UnaryFormula uz = (UnaryFormula) z;
            if(needsInvolution(uz)) return (OperatorFormula) uz.replaceFormula(involution(uz));
            if(needsDeMorganLaw(uz)) return (OperatorFormula) uz.replaceFormula(deMorganLaw(uz));
            return (OperatorFormula) uz.getOperand();
        }
        // if z is a binary formula then
        if(z.isBinary()) {
            BinaryFormula bz = (BinaryFormula) z;
            /* if the junction is in the left branch of the formula
             * and z has AND as operator */
            if(z.isOperator(AND) && this.isLeft()) {
                // if z needs the application of the distributive law
                if(needsDistributiveLaw(bz)) {
                    bz = (BinaryFormula) bz.replaceFormula(distributiveLaw(bz));
                    y = (BinaryFormula) getNextOperator(bz, x.getOperator().getMirrorOperator());
                    return bz;
                }
            }
            /* if the junction is in the right branch of the formula
             * and z has OR as operator */
            if(z.isOperator(OR) && this.isRight()) {
                /* if z needs the application of the distributive law then
                 * apply it and return its result */
                if(needsDistributiveLaw(bz)) {
                    bz = (BinaryFormula) bz.replaceFormula(distributiveLaw(bz));
                    y = (BinaryFormula) getNextOperator(bz, x.getOperator().getMirrorOperator());
                    return bz;
                }
            }
            /* if no rule is applied then
             * if y is in the left subtree of z then return the left child of z
             * if y is in the right subtree of z then return the right child of z */
            if(y.isInLeftSubtreeOf(bz)) return (OperatorFormula) bz.getLoperand();
            if(y.isInRightSubtreeOf(bz)) return (OperatorFormula) bz.getRoperand();
        }
        // if no one of the case above is satisfied then return z;
        return z;
    }

    /** @return Returns true if, and only if, the nodes between x and y have the same operator,
     * for exception to the parent node of y which could have a NOT as operator */
    public boolean isOperatorChain() {
        if(!y.isNestedInside(x)) throw new IllegalArgumentException(
                "x has to be an ancestor of y"
        );
        OperatorFormula p = y.getParent();
        /* while is not reached the node x go up */
        while(p != null && p != x) {
            /* if the operator of p differs from the operator of the chain
             * and is not the negation of y then return false */
            if(!p.isOperator(op)) {
                if(!(p.isParentOf(y) && p.isOperator(NOT))) return false;
            }
            p = p.getParent();
        }
        return true;
    }

    public void setupOperatorChain() {
        BinaryFormula fx = this.getX();
        Formula fy = this.getY();
        if(this.isOperatorChain()) {
            /* if the parent node of y has a NOT operator then replace y with it */
            if(y.getParent().isOperator(NOT)) fy = y.getParent();
            if(op.equals(AND)) {
                if(x.isParentOf(fy)) { rewriteImmediateChild(AND, fy); }
                rearrangeInnerFormula((BinaryFormula) fx.getLoperand(), fy);
            }
            else if(op.equals(OR)) {
                if(x.isParentOf(fy)) rewriteImmediateChild(OR, fy);
                rearrangeInnerFormula((BinaryFormula) fx.getRoperand(), fy);
            }
        }
    }

    private void rewriteImmediateChild(Operator op, Formula c) {
        BinaryFormula cp = (BinaryFormula) c.getParent();
        if(op.equals(AND)) cp.setLoperand (
                new BinaryFormula(AND, new AtomicFormula(TRUE), c)
        );
        else if(op.equals(OR)) cp.setRoperand (
                new BinaryFormula(OR, new AtomicFormula(FALSE), c)
        );
    }

    public BinaryFormula setupJunctionPath() {
        BinaryFormula r = x;

        if (needsLemmaA2AND(x) && y.isInRightSubtreeOf(x)) {
            r = (BinaryFormula) x.replaceFormula(lemmaA2AND(x));
            x = searchX(r);
            y = (BinaryFormula) x.searchOperator(x.getOperator().getMirrorOperator());
        }
        else if (needsLemmaA2OR(x) && y.isInLeftSubtreeOf(x)) {
            r = (BinaryFormula) x.replaceFormula(lemmaA2OR(x));
            x = searchX(r);
            y = (BinaryFormula) x.searchOperator(x.getOperator().getMirrorOperator());
        }
        else if(this.isOperatorChain()) {
            this.setupOperatorChain();
        }
        return r;
    }


    /** @return Returns true if, and only if, the junction has an eliminable form  */
    public boolean eliminableForm() {

        if(op.equals(AND)){
            if(x.getLoperand().isOperator(AND)) {
                BinaryFormula lc = (BinaryFormula) x.getLoperand();
                boolean yIsRightChildOfX = y == lc.getRoperand();
                boolean yParentIsNOT =  y.getParent().isOperator(NOT);
                boolean yParentIsRightChildOfX = y.getParent() == lc.getRoperand();
                return (yIsRightChildOfX || (yParentIsNOT && yParentIsRightChildOfX));
            }
        }
        if(op.equals(OR)) {
            if (x.getRoperand().isOperator(OR)) {
                BinaryFormula rc = (BinaryFormula) x.getRoperand();
                boolean yIsRightChildOfX = y == rc.getRoperand();
                boolean yParentIsNOT =  y.getParent().isOperator(NOT);
                boolean yParentIsRightChildOfX = y.getParent() == rc.getRoperand();
                return (yIsRightChildOfX || (yParentIsNOT && yParentIsRightChildOfX));
            }
        }
        return false;
    }





}
