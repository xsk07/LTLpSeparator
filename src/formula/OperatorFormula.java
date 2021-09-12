package formula;

import static formula.TypeConstant.*;

public abstract class OperatorFormula extends Formula {

    private Operator operator;

    public OperatorFormula(Operator op) {
        super(OPERATOR);
        this.operator = op;
    }

    public OperatorFormula(Operator op, OperatorFormula p) {
        super(OPERATOR, p);
        this.operator = op;
    }

    /** Sets the operator of the formula */
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

    /** @return Returns the image of the top operator of the formula */
    public String getImage(){ return this.operator.getImage(); }

    public abstract OperatorFormula deepCopy();

    /** @return Returns true, if and only if, the tree rooted in this
     * contains an occurrence of op
     * @param op the operator to search the occurrence */
    public boolean hasNestedOccurenceOf(Operator op){
        if(this.isOperator()){
            if(this.isUnary()){

            }
            if(this.isBinary()){

            }
        }
        return false;
    }

}
