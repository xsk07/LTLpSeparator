package formula;

import static formula.TypeConstant.*;

public class OperatorFormula extends Formula {

    Operator operator;

    public OperatorFormula(Operator op) {
        super(OPERATOR);
        this.operator = op;
    }

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

    /** @return Returns a deep copy of the formula */
    public OperatorFormula deepCopy(){
        return new OperatorFormula(this.operator);
    }

}
