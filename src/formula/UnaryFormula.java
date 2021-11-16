package formula;

import static formula.TimeConstant.*;

/** The UnaryFormula class represents an LTL formula which the top operator is of arity one (unary).
 * Each UnaryFormula has a tree structure.
 * The only one child represents the operand in the scope of the top operator. */
public class UnaryFormula extends OperatorFormula {
    Formula operand;

    /** Initializes a newly created UnaryFormula with operator op.
     * @param op The unary operator of the formula
     * @param o The operand of the formula */
    public UnaryFormula(Operator op, Formula o) {
        super(op);
        this.setOperand(o);
    }

    /** Initializes a newly created UnaryFormula with operator op and operand o.
     * @param op The unary operator of the formula
     * @param o  The operand of formula
     * @param p  The parent formula */
    public UnaryFormula(Operator op, Formula o, OperatorFormula p) {
        super(op, p);
        this.setOperand(o);
    }

    /** @return Returns a formula which is the operand of the operator of the formula
     * on which the method was called */
    public Formula getOperand() { return operand; }

    /** Sets the operand of the formula.
     * @param o The formula to be set as the operand */
    public void setOperand(Formula o) {
        this.operand = o;
        o.setParent(this);
    }

    protected void updateTime(Formula o) {
        if(this.getOperand() != o) {
            throw new IllegalArgumentException(
                    "The formula passed as argument must be the operand of this"
            );
        }
        this.setTime(determineTime(this.getOperator(), o.getTime()));
    }

    protected void updateSeparation() {
        TimeConstant operatorTime = this.getOperator().getTime();
        switch (operatorTime) {
            case PAST -> this.setSeparation(!this.containsOperatorOfTime(FUTURE));
            case FUTURE -> this.setSeparation(!this.containsOperatorOfTime(PAST));
            case PRESENT -> this.setSeparation(this.getOperand().getSeparation());
            default -> this.setSeparation(false);
        }
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", this.getOperator(), this.getOperand());
    }

    @Override
    public UnaryFormula deepCopy() {
        return new UnaryFormula(
                this.getOperator(),
                this.operand.deepCopy()
        );
    }

    public boolean equalTo(Formula f) {
        if(f instanceof UnaryFormula uf){
            boolean sameOperator = uf.getOperator().equals(this.getOperator());
            boolean equalOperands = uf.getOperand().equalTo(this.getOperand());
            return sameOperator && equalOperands;
        }
        return false;
    }

}
