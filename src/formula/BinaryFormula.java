package formula;

import java.util.Objects;

/** The BinaryFormula class represents an LTL formula which the root operator is of arity two (binary).
 * Each BinaryFormula has a tree structure. The left and the right children
 * represent the operands of the root operator of the formula. */
public class BinaryFormula extends OperatorFormula {
    private Formula loperand;
    private Formula roperand;

    /** Initializes a newly created BinaryFormula with operator op.
     * @param op The binary operator of the formula */
    public BinaryFormula(Operator op){ super(op); }

    /** Initializes a newly created BinaryFormula with
     * operator op, left operand lOp and right operand rOp.
     * @param op The binary operator of the formula
     * @param lOp The left operand
     * @param rOp the right operand */
    public BinaryFormula(Operator op, Formula lOp, Formula rOp){
        super(op);
        this.setLoperand(lOp);
        this.setRoperand(rOp);
    }

    /** Initializes a newly created BinaryFormula with
     * operator op, left operand lOp and right operand rOp.
     * @param op The binary operator of the formula
     * @param lOp The left operand
     * @param rOp the right operand */
    public BinaryFormula(Operator op, Formula lOp, Formula rOp, OperatorFormula p){
        super(op, p);
        this.setLoperand(lOp);
        this.setRoperand(rOp);
    }

    /** Sets the left operand of the formula and updates the parent of the formula corresponding to the left operand.
     * @param f The formula to be set as left operand */
    public void setLoperand(Formula f){
        this.loperand = f;
        f.setParent(this);
    }

    /** Sets the right operand of the formula and updates the parent of the formula corresponding to the right operand.
     * @param f The formula to be set as right operand */
    public void setRoperand(Formula f){
        this.roperand = f;
        f.setParent(this);
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
    public boolean isLeftChild(Formula f) {return Objects.equals(this.getLoperand(), f);}

    /** @return Returns true, if and only if, f is the right operand of the formula
     * on which the method was called
     * @param f the formula to compare */
    public boolean isRightChild(Formula f) {return Objects.equals(this.getRoperand(), f);}

    @Override
    public String toString(){
        String str = "";

        // left operand string
        if(loperand.isAtomic()) str += loperand.toString(); // if the left operand is an atom
        else {
            if(loperand.isOperator()) { // if the left operand is an operator then
                OperatorFormula loperandF = (OperatorFormula) loperand;
                if(loperandF.isUnary() || loperandF.getOperator() == this.getOperator()) {
                    str += loperand.toString();
                }
                else str += "(" + loperand.toString() + ")"; // if is a binary formula add the parentheses
            }
            else str += "(" + loperand.toString() + ")";
        }

        // operator string
        str += super.getOperator().getImage();

        // right operand string
        if(roperand.isAtomic()) str += roperand.toString();
        else {
            if(roperand.isOperator()) {
                OperatorFormula roperandF = (OperatorFormula) roperand;
                if(roperandF.isUnary() || roperandF.getOperator() == this.getOperator()) {
                    str += roperand.toString();
                }
                else str += "(" + roperand.toString() + ")";
            }
            else str += "(" + roperand.toString() + ")";
        }
        return str;
    }

    @Override
    public BinaryFormula deepCopy(){
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


    @Override
    public boolean equals(Formula f) {
        if(f.isOperator()) {
            OperatorFormula of = (OperatorFormula) f;
            if(of.isBinary()){
                BinaryFormula bf = (BinaryFormula) of;
                return (
                        (bf.isOperator(this.getOperator()))
                                && (bf.getLoperand().equals(this.getLoperand()))
                                && (bf.getRoperand().equals(this.getRoperand()))
                );
            }
        }
        return false;
    }

    /** @return Returns true, if and only if, the formula f is found inside the left operand subtree
     * @param f The formula to search */
    public boolean inLeftSubtree(Formula f){
        Formula nf = f;
        while(nf.getParent() != this && nf.getParent() != null) nf = nf.getParent();
        if(nf.getParent().equals(this) && nf.equals(this.getLoperand())) return true;
        return false;
    }

    /** @return Returns true, if and only if, the formula f is found inside the right operand subtree
     * @param f The formula to search */
    public boolean inRightSubtree(Formula f){
        Formula nf = f;
        while(nf.getParent() != this && nf.getParent() != null) nf = nf.getParent();
        if(nf.getParent().equals(this) && nf.equals(this.getRoperand())) return true;
        return false;
    }

}
