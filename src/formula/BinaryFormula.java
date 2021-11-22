package formula;

import java.util.*;
import static formula.Operator.*;
import static formula.TimeConstant.*;

/** The BinaryFormula class represents an LTL formula which the root operator is of arity two (binary).
 * Each BinaryFormula has a tree structure. The left and the right children
 * represent the operands of the root operator of the formula. */
public class BinaryFormula extends OperatorFormula {
    private Formula loperand;
    private Formula roperand;

    /** Initializes a newly created BinaryFormula with
     * operator op, left operand lc and right operand rc.
     * @param op The binary operator of the formula
     * @param lc The left operand
     * @param rc the right operand */
    public BinaryFormula(Operator op, Formula lc, Formula rc) {
        super(op);
        this.setLoperand(lc);
        this.setRoperand(rc);
    }

    /** Initializes a newly created BinaryFormula with
     * operator op, left operand lc and right operand rc.
     * @param op The binary operator of the formula
     * @param lc The left operand
     * @param rc the right operand */
    public BinaryFormula(Operator op, Formula lc, Formula rc, OperatorFormula p) {
        super(op,p);
        this.setLoperand(lc);
        this.setRoperand(rc);
    }

    /** Sets the left operand of the formula and updates the parent of the formula corresponding to the left operand.
     * @param f The formula to be set as left operand */
    public void setLoperand(Formula f) {
        this.loperand = f;
        f.setParent(this);
    }

    /** Sets the right operand of the formula and updates the parent of the formula corresponding to the right operand.
     * @param f The formula to be set as right operand */
    public void setRoperand(Formula f) {
        this.roperand = f;
        f.setParent(this);
    }

    protected void updateTime(Formula f) {
        if(!f.isChildOf(this)) {
            throw new IllegalArgumentException(
                    "The formula passed as argument must be one of the two children of this"
            );
        }
        /* if the formula f was set as the left child then
         * update the time of this */
        if(f.isLeftChildOf(this)) {
            Formula rc = this.getRoperand();
            if(rc != null) this.setTime(
                    TimeConstant.determineTime(
                            this.getOperator(),
                            f.getTime(),
                            rc.getTime()
                    )
            );
        }
        else if(f.isRightChildOf(this)) {
            Formula lc = this.getLoperand();
            if(lc != null) this.setTime(
                    TimeConstant.determineTime(
                            this.getOperator(),
                            lc.getTime(),
                            f.getTime()
                    )
            );
        }
    }

    protected void updateSeparation() {
        boolean leftOperandSeparationValue = false;
        boolean rightOperandSeparationValue = false;
        if(this.getLoperand() != null) {
            leftOperandSeparationValue = this.getLoperand().getSeparation();
        }
        if(this.getRoperand() != null) {
            rightOperandSeparationValue = this.getRoperand().getSeparation();
        }
        TimeConstant operatorTime = this.getOperator().getTime();
        switch (operatorTime) {
            case PAST -> this.setSeparation(!this.containsOperatorOfTime(FUTURE));
            case FUTURE -> this.setSeparation(!this.containsOperatorOfTime(PAST));
            case PRESENT -> this.setSeparation(
                    leftOperandSeparationValue && rightOperandSeparationValue
            );
            default -> this.setSeparation(false);
        }
    }

    /** @return Returns the left operand formula */
    public Formula getLoperand(){ return loperand; }

    /** @return Returns the right operand formula */
    public Formula getRoperand(){
        return roperand;
    }

    /** @return Returns true, if and only if, f is the left operand of the formula
     * on which the method was called
     * @param f the formula to compare */
    public boolean isLeftChild(Formula f) {
        return  this.getLoperand() == f;
    }

    /** @return Returns true, if and only if, f is the right operand of the formula
     * on which the method was called
     * @param f the formula to compare */
    public boolean isRightChild(Formula f) {
        return this.getRoperand() == f;
    }


    @Override
    public String toString() {

        String leftChild = this.getLoperand().toString();
        String rightChild = this.getRoperand().toString();
        if(this.getLoperand() instanceof OperatorFormula) {
            leftChild = String.format("(%s)", this.getLoperand().toString());
        }
        if(this.getRoperand() instanceof OperatorFormula) {
            rightChild = String.format("(%s)", this.getRoperand().toString());
        }

        if(this.isOperator(UNTIL) || this.isOperator(SINCE)) {
            String swapString = leftChild;
            leftChild = rightChild;
            rightChild = swapString;
        }

        return String.format("%s %s %s", leftChild, this.getOperator(), rightChild);
    }

    @Override
    public BinaryFormula deepCopy() {
        return new BinaryFormula(
                this.getOperator(),
                this.loperand.deepCopy(),
                this.roperand.deepCopy()
        );
    }

    /** Swaps the two operands of the formula. */
    public void swapChildren(){
        Formula temp = this.loperand;
        this.loperand = this.roperand;
        this.roperand = temp;
    }

    public boolean equalTo(Formula f) {
        if(f instanceof OperatorFormula of) {
            if(of instanceof BinaryFormula bf){
                boolean sameOperator = bf.isOperator(this.getOperator());
                boolean sameLeftOperand = bf.getLoperand().equalTo(this.getLoperand());
                boolean sameRightOperand = bf.getRoperand().equalTo(this.getRoperand());
                boolean sameOperands = sameLeftOperand && sameRightOperand;
                if(this.isOperator(AND) || this.isOperator(OR) || this.isOperator(EQUIV)) {
                    sameOperands = this.equalCombinations(bf);
                }
                return sameOperator && sameOperands;
            }
        }
        return false;
    }

    private boolean equalCombinations(BinaryFormula f) {
        ArrayList<Formula> fOperands = f.getCombinationOperands();
        ArrayList<Formula> tOperands = this.getCombinationOperands();
        if(fOperands.size() == tOperands.size()) {
            tOperands.forEach(t -> {
                for(Formula af : fOperands) {
                    if(af.equalTo(t)) {
                        fOperands.remove(af);
                        break;
                    }
                }
            } );
            return fOperands.isEmpty();
        }
        return false;
    }

    public boolean isNestedInsideMirror() {
        if(this.isOperator(UNTIL) || this.isOperator(Operator.SINCE)){
            Formula nf = this;
            while(nf!= null && !nf.isOperator(this.getOperator().getMirrorOperator())){
                nf = nf.getParent();
            }
            if(nf!= null) return nf.isOperator(this.getOperator().getMirrorOperator());
            return false;
        }
        return false;
    }

    /** Returns a new BinaryFormula that is the disjunction of the formulas got in input
      * @param fms a Collection of formulas
      * @return a new BinaryFormula that is the disjunction of the formulas of the collection
      * got in input */
    public static Formula newDisjunction(Collection<Formula> fms) {
        return newCombination(OR, fms);
    }

    /** Returns a new BinaryFormula that is the conjunction of the formulas got in input
     * @param fms a Collection of formulas
     * @return a new BinaryFormula that is the conjunction of the formulas of the collection
     * got in input */
    public static Formula newConjunction(Collection<Formula> fms) {
        return newCombination(AND, fms);
    }

    public static Formula newCombination(Operator op, Collection<Formula> fms) {
        if(fms.size() == 1) return fms.iterator().next();
        return combine(op, fms);
    }

    private static BinaryFormula combine(Operator op, Collection<Formula> fms) {
        if(!op.isBinary()) throw new IllegalArgumentException(
                "the operator must be binary"
        );
        if(fms.size() < 2) throw new IndexOutOfBoundsException (
                "the list must contain at least two elements"
        );
        Iterator<Formula> itr = fms.iterator();
        Formula prev = itr.next();
        while (itr.hasNext()) prev = new BinaryFormula(op, prev.deepCopy(), itr.next().deepCopy());
        return (BinaryFormula) prev;
    }

    public ArrayList<Formula> getCombinationOperands() {
        ArrayList<Formula> al = new ArrayList<>();
        Queue<BinaryFormula> q = new LinkedList<>();
        q.add(this);
        while(!q.isEmpty()) {
            BinaryFormula nf = q.remove();
            /* if the left operand have the same operator of the root add it to the
             * loop queue else add it to the result array list */
            if(nf.getLoperand().isOperator(this.getOperator())) {
                q.add((BinaryFormula) nf.getLoperand());
            }
            else al.add(nf.getLoperand());
            /* if the right operand have the same operator of the root add it to the
             * loop queue else add it to the result array list */
            if(nf.getRoperand().isOperator(this.getOperator())) {
                q.add((BinaryFormula) nf.getRoperand());
            }
            else al.add(nf.getRoperand());
        }
        return al;
    }


}
