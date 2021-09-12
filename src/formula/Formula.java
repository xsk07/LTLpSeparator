package formula;

import java.util.Objects;
import static formula.Operator.*;
import static formula.TypeConstant.*;


/** The Formula class represents a generic LTL formula. */
public abstract class Formula {

    /** The parent of the formula */
    private OperatorFormula parent;

    /** The type of the formula */
    private final TypeConstant type;

    /** Initializes a newly created Formula with type t.
     * @param t type of the formula */
    public Formula(TypeConstant t) {
        this.parent = null;
        this.type = t;
    }

    /** Initializes a newly created Formula with type t and parent p.
     * @param t type of the formula
     * @param p the parent of the formula */
    public Formula(TypeConstant t, OperatorFormula p) {
        this.parent = p;
        this.type = t;
    }

    /** Sets the parent of the formula
     * @param p the formula which will be set as parent */
    public void setParent(OperatorFormula p) { this.parent = p; }

    /** @return Returns the parent of the formula */
    public OperatorFormula getParent(){ return this.parent; }

    /** @return Returns a TypeConstant which is type of the formula
     * @see TypeConstant*/
    public TypeConstant getType(){ return type; }

    /** @return Returns true if, and only if, the type of the formula is OPERATOR */
    public boolean isOperator() {
        return type == OPERATOR;
    }

    /** @return Returns true if, and only if, the type of the formula is OPERATOR and
     * its image corresponds to the image of the operator op */
    public boolean isOperator(Operator op){
        return this.isOperator() && Objects.equals(this.getImage(), op.getImage());
    }

    /** @return Returns true if, and only if, the type of the formula is ATOM */
    public boolean isAtomic() {
        return type == ATOM;
    }

    /** @return Returns a deep copy of the formula */
    public abstract Formula deepCopy();

    /** @return Returns the negation of the formula on which the method was called */
    public UnaryFormula negate() {
        return new UnaryFormula(NOT, this.deepCopy(), null);
    }

    /** @return Returns the string image of the formula */
    public abstract String getImage();

    public abstract boolean equals(Formula f);




}
