package formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalInt;
import static formula.Operator.*;
import static separator.FormulaSeparator.getNextOperator;
import static formula.BooleanRules.*;
import static formula.AtomConstant.*;

public class Junction extends Path {

    private Operator operator;

    public Junction(BinaryFormula fx, BinaryFormula fy) {

        super(fx, fy);

        /* if y is nested inside the left subtree of x then
        * set the operator of the junction to AND */
        if(fy.isInLeftSubtreeOf(fx)) operator = AND;

        /* if y is nested inside the right subtree of x then
        * set the operator of the junction to OR */
        if(fy.isInRightSubtreeOf(fx)) operator = OR;

    }

    /** @return Returns the ruling operator of the junction path */
    public Operator getOperator() { return operator; }

    /** @return Returns true if, and only if, the junction path is inside the left
     * subtree of the root node of the junction */
    public boolean isLeft() { return operator.equals(AND); }

    /** @return Returns true if, and only if, the junction path is inside the right
     * subtree of the root node of the junction */
    public boolean isRight() { return operator.equals(OR); }

    public boolean equals(Junction j) {
        boolean sameX = this.getX().equals(j.getX());
        boolean sameY = this.getY().equalTo(j.getY());
        boolean sameOp = this.getOperator().equals(j.getOperator());
        return sameX && sameY && sameOp;
    }

    /** The number of nodes over the junction with the same operator of x. */
    public int getN() {
        int n = 0;
        OperatorFormula x = (OperatorFormula) this.getX();
        Formula z = this.getX();
        while (z != null) {
            if(z.isOperator(x.getOperator())) n += 1;
            z = z.getParent();
        }
        return n;
    }

    /** The number of nodes under the junction with the same operator of y. */
    public int getK() {
        OperatorFormula y = (OperatorFormula) this.getY();
        ArrayList<Path> paths = y.getPaths();
        OptionalInt k =  paths.stream().mapToInt(
                p -> p.countOperatorOccurrences(y.getOperator())
        ).max();
        if(k.isPresent()) return k.getAsInt();
        return 0;
    }

    public static ArrayList<Junction> junctionsList(BinaryFormula x, Collection<OperatorFormula> ys) {
        ArrayList<Junction> js = new ArrayList<>();
        ys.forEach(y -> js.add(new Junction(x, (BinaryFormula) y)));
        return js;
    }

    /** @return Returns true if, and only if, the junction path is arranged,
     * it means that no rule could be applied on the nodes of the junction path */
    public boolean isArranged() {
        OperatorFormula z = (OperatorFormula) this.getX();
        while(z != this.getY()) {
            if(z.isOperator(NOT)) {
                assert z instanceof UnaryFormula;
                UnaryFormula uz = (UnaryFormula) z;
                if(needsInvolution(uz)) return false;
                if(needsDeMorganLaw(uz)) return false;
                z = (OperatorFormula) uz.getOperand();
            }
            // if z is a binary formula then
            if(z instanceof BinaryFormula bz) {
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
                if(this.getY().isInLeftSubtreeOf(bz)) z = (OperatorFormula) bz.getLoperand();
                if(this.getY().isInRightSubtreeOf(bz)) z = (OperatorFormula) bz.getRoperand();
            }
        }
        return true;
    }


    /** @return returns true if, and only if, the junction path needs to be arranged */
    public boolean needsArrangement(){ return !isArranged(); }

    /** Arranges the junction path. */
    public void arrange() {
        while(this.needsArrangement()) {
            OperatorFormula z = (OperatorFormula) this.getX();
            while (z != this.getY()) z = this.applyArrangement(z);
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
        if(z instanceof BinaryFormula bz) {
            /* if the junction is in the left branch of the formula
             * and z has AND as operator */
            if(z.isOperator(AND) && this.isLeft()) {
                // if z needs the application of the distributive law
                if(needsDistributiveLaw(bz)) {
                    bz = (BinaryFormula) bz.replaceFormula(distributiveLaw(bz));
                    OperatorFormula x = (OperatorFormula) this.getX();
                    this.setY(getNextOperator(bz, x.getOperator().getMirrorOperator()));
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
                    OperatorFormula x = (OperatorFormula) this.getX();

                    this.setY(getNextOperator(bz, x.getOperator().getMirrorOperator()));
                    return bz;
                }
            }
            /* if no rule is applied then
             * if y is in the left subtree of z then return the left child of z
             * if y is in the right subtree of z then return the right child of z */
            Formula y = this.getY();
            if(y.isInLeftSubtreeOf(bz)) return (OperatorFormula) bz.getLoperand();
            if(y.isInRightSubtreeOf(bz)) return (OperatorFormula) bz.getRoperand();
        }
        // if no one of the case above is satisfied then return z;
        return z;
    }

    /** @return Returns true if, and only if, the nodes between x and y have the same operator,
     * for exception to the parent node of y which could have a NOT as operator */
    public boolean isOperatorCombination() {
        OperatorFormula x = (OperatorFormula) this.getX();
        Formula y = this.getY();
        if(!x.isAncestorOf(y)) throw new IllegalArgumentException(
                "x must to be an ancestor of y"
        );
        OperatorFormula p = y.getParent();
        /* while is not reached the node x go up */
        while(p != null && p != x) {
            /* if the operator of p differs from the operator of the chain
             * and is not the negation of y then return false */
            if(!p.isOperator(operator)) {
                if(!(p.isParentOf(y) && p.isOperator(NOT))) return false;
            }
            p = p.getParent();
        }
        return true;
    }

    public boolean isImmediateChild() {
        Formula x = this.getX();
        Formula y = this.getY();
        if(y.getParent() == x) return true;
        if(y.getParent().isOperator(NOT) && (y.getParent().getParent() == x)) return true;
        return false;
    }

    public void rewriteImmediateChild() {
        BinaryFormula x = (BinaryFormula) this.getX();
        Formula y = this.getY();
        Formula c = y;
        if(y.getParent().isOperator(NOT)) c = y.getParent();
        if(operator.equals(AND)) x.setLoperand(new BinaryFormula(AND, new AtomicFormula(TRUE), c));
        if(operator.equals(OR)) x.setRoperand(new BinaryFormula(OR, new AtomicFormula(FALSE), c));
    }

}
