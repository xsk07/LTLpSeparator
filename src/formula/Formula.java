package formula;

import static formula.TypeConstant.*;


/** The Formula class represents a generic LTL formula. */
public class Formula {

    /** The type of the formula */
    public TypeConstant type;

    /** Initializes a newly created Formula with type t.
     * @param t type of the formula */
    public Formula(TypeConstant t) { this.type = t; }

    /** @return Returns a TypeConstant which is type of the formula
     * @see TypeConstant*/
    public TypeConstant getType(){ return type; }

    /** @return Returns true if, and only if, the type of the formula is OPERATOR */
    public boolean isOperator() {
        return type == OPERATOR;
    }

    /** @return Returns true if, and only if, the type of the formula is ATOM */
    public boolean isAtomic() {
        return type == ATOM;
    }

    /** @return Returns a deep copy of the formula */
    public Formula deepCopy() {
        return new Formula(this.getType());
    }

}
