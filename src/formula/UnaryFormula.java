package formula;

import java.util.Objects;

/** The UnaryFormula class represents an LTL formula which the top operator is of arity one (unary).
 * Each UnaryFormula has a tree structure.
 * The only one child represents the operand in the scope of the top operator. */
public class UnaryFormula extends OperatorFormula {
    Formula operand;

    /** Initializes a newly created UnaryFormula with operator op.
     * @param op The unary operator of the formula */
    public UnaryFormula(Operator op) {
        super(op);
    }

    /** Initializes a newly created UnaryFormula with operator op and operand o.
     * @param op The unary operator of the formula
     * @param o  The operand of formula */
    public UnaryFormula(Operator op, Formula o) {
        super(op);
        this.setOperand(o);
    }


    /** @return Returns a formula which is the operand of the operator of the formula
     * on which the method was called */
    public Formula getOperand() {
        return operand;
    }

    /** Sets the operand of the formula.
     * @param op The formula to be set as the operand */
    public void setOperand(Formula op) {
        this.operand = op;
    }

    @Override
    public String toString() {
        String str = this.getOperator().getImage();
        if(operand.isAtomic()) {
            AtomicFormula operandA = (AtomicFormula) operand;
            str += operandA.toString();
        }
        else {
            if(operand.isOperator()) {
                OperatorFormula operandF = (OperatorFormula) operand;
                if(operandF.isUnary()) {
                    UnaryFormula operandU = (UnaryFormula) operandF;
                    str += operandU.toString();
                }
                if(operandF.isBinary()) {
                    BinaryFormula operandB = (BinaryFormula) operandF;
                    str += "(" + operandB.toString() + ")";
                }
            }
            else str += "(" + operand.toString() + ")";
        }
        return str;
    }

    @Override
    public UnaryFormula deepCopy() {
        return new UnaryFormula(
                this.getOperator(),
                operand.deepCopy()
        );
    }

    @Override
    public boolean equals(Formula f) {
        if(f.isOperator()) {
            OperatorFormula of = (OperatorFormula) f;
            if(of.isUnary()){
                UnaryFormula uf = (UnaryFormula) of;
                return (
                        (uf.getOperator() == this.getOperator())
                        && (uf.getOperand().equals(this.getOperand()))
                        );
            }
        }
        return false;
    }


}
