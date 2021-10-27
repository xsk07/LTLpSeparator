package formula;

import static formula.TypeConstant.*;
import static formula.AtomConstant.*;
import static formula.TimeConstant.*;

/** The AtomicFormula class represents an atomic LTL formula. */
public class AtomicFormula extends Formula {

    /** String image of the formula */
    private String image;

    /* Initialization block:
     * atomic formulas are always present and separated */
    {
        this.setTime(PRESENT);
        this.setSeparation(true);
    }

    /** Returns an AtomicFormula created from an AtomConstant.
     * @see AtomConstant */
    public AtomicFormula(AtomConstant c) {
        super(ATOM);
        this.image = c.getImage();
    }

    /** Initializes a newly created AtomicFormula from an AtomConstant and a formula which will be its parent.
     * @see AtomConstant */
    public AtomicFormula(AtomConstant c, OperatorFormula p) {
        super(ATOM, p);
        this.image = c.getImage();
    }

    /** Returns an AtomicFormula created from a string image. */
    public AtomicFormula(String img) {
        super(ATOM);
        this.image = img;
    }

    /** Initializes a newly created AtomicFormula from a string image and a formula which will be its parent. */
    public AtomicFormula(String img, OperatorFormula p) {
        super(ATOM, p);
        this.image = img;
    }

    /** Sets the image of the formula. */
    public void setImage(String img) { this.image = img; }

    /** @return Returns the image of the formula */
    public String getImage() { return image; }

    @Override
    public String toString() { return image; }

    /** @return Returns a deep copy of the formula */
    public AtomicFormula deepCopy() {
        return new AtomicFormula(this.getImage());
    }

    @Override
    public boolean equals(Formula f) {
        return f.isAtomic() && this.getImage().equals(f.getImage());
    }

    /** @return Returns true if, and only if, the formula represents the atomic "true" formula */
    public boolean isTrue(){
        return image.equals(TRUE.getImage());
    }

    /** @return Returns true if, and only if, the formula represents the atomic "false" formula */
    public boolean isFalse(){
        return image.equals(FALSE.getImage());
    }

    /** @return Returns true if, and only if, the formula represents a truth value */
    public boolean isTruthValue(){
        return (this.isTrue() || this.isFalse());
    }

}
