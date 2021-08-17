package formula;

/** The UnaryFormula class represents an LTL formula which the top operator is of arity one (unary).
 * Each UnaryFormula has a tree structure.
 * The only one child represents the operand in the scope of the top operator. */
public class UnaryFormula extends OperatorFormula {
    Formula operand;

    public UnaryFormula(Operator op) {
        super(op);
    }

    /** @return Returns a formula which is the operand of the formula */
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
        String str = this.operator.getImage();
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

    /** @return Returns a deep copy of the formula */
    public UnaryFormula deepCopy() {
        UnaryFormula f = new UnaryFormula(this.operator);
        f.operand = operand.deepCopy();
        return f;
    }

}
