package formula;

/** The BinaryFormula class represents an LTL formula which the top operator is of arity two (binary).
 * Each BinaryFormula has a tree structure. The left and the right children
 * represent the operands of the top operator of the formula. */
public class BinaryFormula extends OperatorFormula {
    private Formula loperand;
    private Formula roperand;

    /** Initializes a newly created BinaryFormula with operator op.
     * @param op The binary operator of the formula */
    public BinaryFormula(Operator op){
        super(op);
    }

    /** Sets the left operand of the formula.
     * @param phi The formula to be set as left operand */
    public void setLoperand(Formula phi){
        this.loperand = phi;
    }

    /** Sets the right operand of the formula.
     * @param phi The formula to be set as right operand */
    public void setRoperand(Formula phi){
        this.roperand = phi;
    }

    /** @return Returns the left operand formula */
    public Formula getLoperand(){ return loperand; }

    /** @return Returns the right operand formula */
    public Formula getRoperand(){
        return roperand;
    }


    @Override
    public String toString(){
        String str = "";

        // left operand string
        if(loperand.isAtomic()) str += loperand.toString(); // if the left operand is an atom
        else {
            if(loperand.isOperator()) { // if the left operand is an operator then
                OperatorFormula loperandF = (OperatorFormula) loperand;
                if(loperandF.isUnary()) str += loperand.toString();
                else str += "(" + loperand.toString() + ")"; // if is a binary formula add the parentheses
            }
            else str += "(" + loperand.toString() + ")";
        }

        // operator string
        str += super.operator.getImage();

        // right operand string
        if(roperand.isAtomic()) str += roperand.toString();
        else {
            if(roperand.isOperator()) {
                OperatorFormula roperandF = (OperatorFormula) roperand;
                if(roperandF.isUnary()) str += roperand.toString();
                else str += "(" + roperand.toString() + ")";
            }
            else str += "(" + roperand.toString() + ")";
        }
        return str;
    }

    @Override
    public BinaryFormula deepCopy(){
        BinaryFormula f = new BinaryFormula(this.getOperator());
        f.loperand = loperand.deepCopy();
        f.roperand = roperand.deepCopy();
        return f;
    }

}
