package formula;

import static formula.TypeConstant.*;

public abstract class OperatorFormula extends Formula {

    private Operator operator;

    public OperatorFormula(Operator op) {
        super(OPERATOR);
        this.setOperator(op);
    }

    public OperatorFormula(Operator op, OperatorFormula p) {
        super(OPERATOR, p);
        this.setOperator(op);
    }

    /** Sets the operator of the formula */
    public void setOperator(Operator op) {
        this.operator = op;
        this.setTime(op.getTime());
        this.setSeparation(true);
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

    public boolean isParentOf(Formula f){
        return f.getParent().equals(this);
    }

    public boolean isAncestorOf(Formula f) {
        OperatorFormula p = f.getParent();
        while (p != null && !p.equals(this)) {
            p = p.getParent();
        }
        return (p != null && p.equals(this));
    }

    /** @return Returns the image of the top operator of the formula */
    public String getImage(){ return operator.getImage(); }

    protected abstract void updateTime(Formula f);

    protected abstract void updateSeparation();

}
