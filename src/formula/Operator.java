package formula;

import static formula.TimeConstant.*;

/** The Operator class represents the entire set of operators */
public enum Operator {

    NOT (PRESENT,"!", 1),
    AND (PRESENT,"&", 2),
    OR (PRESENT,"|", 2),
    IMPL (PRESENT,"->", 2),
    EQUIV (PRESENT,"<->", 2),
    UNTIL (FUTURE,"U", 2),
    SINCE (PAST,"S", 2),
    UNLESS (FUTURE,"W", 2),
    ONCE (PAST,"O", 1),
    HIST (PAST,"H", 1),
    YEST (PAST,"Y", 1),
    FIN (FUTURE,"F", 1),
    GLOB (FUTURE,"G", 1),
    NEXT (FUTURE,"X", 1);

    private final TimeConstant time;
    private final int arity;
    private final String image;
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

    Operator(TimeConstant t, String img, int n) {
        this.time = t;
        this.image = img;
        this.arity = n;
    }

    public TimeConstant getTime(){ return time; }

    /** @return Returns true if, and only if, is a past operator */
    public boolean isPast(){return time == PAST;}

    /** @return Returns true if, and only if, is a present operator */
    public boolean isPresent(){return time == PRESENT;}

    /** @return Returns true if, and only if, is a past operator */
    public boolean isFuture(){return time == FUTURE;}

    /** @return Returns the arity of the operator */
    public int getArity() { return arity; }

    /** @return Returns true if, and only if, it is a unary operator */
    public boolean isUnary() {return arity == 1; }

    /** @return Returns true if, and only if, it is a binary operator */
    public boolean isBinary() {return arity == 2; }

    /** @return Returns the image of the operator */
    public String getImage() { return image; }

    /** @return Returns true if, and only if, has a mirror operator */
    public boolean hasMirrorOperator(){ return mirrorOperator != null; }

    /** @return Returns the mirror operator */
    public Operator getMirrorOperator() {
        if(hasMirrorOperator()) return mirrorOperator;
        else return null; // should throw an exception !!!
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
