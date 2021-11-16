package formula;

public abstract class OperatorFormula extends Formula {

    private Operator operator;

    public OperatorFormula(Operator op) {
        super();
        this.setOperator(op);
    }

    public OperatorFormula(Operator op, OperatorFormula p) {
        super(p);
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

    public boolean isParentOf(Formula f){ return f.getParent() == this; }

    public boolean isAncestorOf(Formula f) {
        OperatorFormula p = f.getParent();
        while (p != null && p != this) p = p.getParent();
        return p == this;
    }

    /** @return Returns the image of the top operator of the formula */
    public String getImage(){ return operator.getImage(); }

    protected abstract void updateTime(Formula f);

    protected abstract void updateSeparation();

}
