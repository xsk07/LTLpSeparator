package formula;

import static formula.TypeConstant.*;

/** The AtomicFormula class represents an atomic LTL formula. */
public class AtomicFormula extends Formula {

    /** String image of the formula */
    private String image;

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
    public String getImage() {
        return this.image;
    }

    @Override
    public String toString() { return this.image; }

    /** @return Returns a deep copy of the formula */
    public AtomicFormula deepCopy() {
        return new AtomicFormula(this.getImage());
    }

    @Override
    public boolean equals(Formula f) {
        return f.isAtomic() && this.getImage().equals(f.getImage());
    }

}
