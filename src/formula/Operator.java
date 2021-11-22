package formula;

import static formula.TimeConstant.*;

/** The Operator class represents the entire set of operators */
public enum Operator {

    NOT (PRESENT,"!", 1, false),
    AND (PRESENT,"&", 2, false),
    OR (PRESENT,"|", 2, false),
    IMPL (PRESENT,"->", 2, true),
    EQUIV (PRESENT,"<->", 2, true),
    UNTIL (FUTURE,"U", 2, false),
    SINCE (PAST,"S", 2, false),
    UNLESS (FUTURE,"W", 2, true),
    ONCE (PAST,"O", 1, true),
    HIST (PAST,"H", 1, true),
    YEST (PAST,"Y", 1, true),
    FIN (FUTURE,"F", 1, true),
    GLOB (FUTURE,"G", 1, true),
    NEXT (FUTURE,"X", 1, true);

    private final TimeConstant time;
    private final int arity;
    private final String image;
    private final boolean derived;
    private Operator mirrorOperator;

    static {

        AND.mirrorOperator = OR;
        OR.mirrorOperator = AND;

        UNTIL.mirrorOperator = SINCE;
        SINCE.mirrorOperator = UNTIL;

        ONCE.mirrorOperator = FIN;
        FIN.mirrorOperator = ONCE;

        HIST.mirrorOperator = GLOB;
        GLOB.mirrorOperator = HIST;

        YEST.mirrorOperator = NEXT;
        NEXT.mirrorOperator = YEST;

    }

    Operator(TimeConstant t, String img, int n, boolean b) {
        this.time = t;
        this.image = img;
        this.arity = n;
        this.derived = b;
    }

    /** @return Returns the time of the operator */
    public TimeConstant getTime(){ return time; }

    /** @return Returns true if, and only if, is a past operator */
    public boolean isPast(){ return time == PAST; }

    /** @return Returns true if, and only if, is a present operator */
    public boolean isPresent(){ return time == PRESENT; }

    /** @return Returns true if, and only if, is a past operator */
    public boolean isFuture(){ return time == FUTURE; }

    /** @return Returns true if, and only if, it is a temporal operator i.e.
      * when is a past or a future operator */
    public boolean isTemporal(){ return (this.isPast() || this.isFuture()); }

    /** @return Returns the arity of the operator */
    public int getArity() { return arity; }

    /** @return Returns true if, and only if, it is a unary operator */
    public boolean isUnary() {return arity == 1; }

    /** @return Returns true if, and only if, it is a binary operator */
    public boolean isBinary() {return arity == 2; }

    /** @return Returns true if, and only if, it is derivated from
      * the basic operators */
    public boolean isDerived() { return derived; }

    /** @return Returns the image of the operator */
    public String getImage() { return image; }

    /** @return Returns true if, and only if, has a mirror operator */
    public boolean hasMirrorOperator(){ return mirrorOperator != null; }

    /** @return Returns the mirror operator */
    public Operator getMirrorOperator() {
        if(hasMirrorOperator()) return mirrorOperator;
        return null;
    }

    @Override
    public String toString(){ return image; }

    /** @return Returns the OperatorConstant corresponding to the string in input
     * @param str A string image */
    public static Operator fromString(String str) {
        for (Operator op : Operator.values()) {
            if (op.image.equals(str)) return op;
        }
        return null;
    }

}
