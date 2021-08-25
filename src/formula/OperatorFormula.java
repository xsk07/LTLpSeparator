package formula;

import static formula.TypeConstant.*;

public abstract class OperatorFormula extends Formula {

    private Operator operator;

    public OperatorFormula(Operator op) {
        super(OPERATOR);
        this.setOperator(op);
    }

    /** @return Sets the operator of the formula */
    public void setOperator(Operator op) { this.operator = op; }

    /** @return Returns the operator of the formula */
    public Operator getOperator(){
        return operator;
    }


    /** @return Returns true if, and only if, the arity of the operator of the formula is 1 */
    public boolean isUnary() {
        return operator.getArity() == 1;
    }

    /** @return Returns true if, and only if, the arity of the operator of the formula is 2 */
    public boolean isBinary() {
        return operator.getArity() == 2;
    }

}
