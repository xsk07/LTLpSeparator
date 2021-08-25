package formula;

/** The Operator class represents the entire set of operators */
public enum Operator {

    NOT (false,"!", 1),
    AND (false,"&", 2),
    OR (false,"|", 2),
    IMPL (false,"->", 2),
    EQUIV (false,"<->", 2),
    UNTIL (true,"U", 2),
    SINCE (true,"S", 2),
    UNLESS (true,"W", 2),
    ONCE (true,"O", 1),
    HIST (true,"H", 1),
    YEST (true,"Y", 1),
    FIN (true,"F", 1),
    GLOB (true,"G", 1),
    NEXT (true,"X", 1);

    private final boolean temporal;
    private final int arity;
    private final String image;
    private Operator mirrorOperator;

    static {

        UNTIL.mirrorOperator = SINCE;
        SINCE.mirrorOperator = UNTIL;

        ONCE.mirrorOperator = FIN;
        FIN.mirrorOperator = ONCE;

        HIST.mirrorOperator = GLOB;
        GLOB.mirrorOperator = HIST;

        YEST.mirrorOperator = NEXT;
        NEXT.mirrorOperator = YEST;

    }

    Operator(boolean temp, String img, int n) {
        this.temporal = temp;
        this.image = img;
        this.arity = n;
        mirrorOperator = null;
    }

    /** @return Returns true if, and only if, represents a temporal operator */
    public boolean isTemporal() { return temporal; }

    /** @return Returns the arity of the operator */
    public int getArity() { return arity; }

    /** @return Returns the image of the operator */
    public String getImage() { return image; }

    /** @return Returns the mirror operator */
    public Operator getMirrorOperator() { return mirrorOperator; }

    /** @return Returns the OperatorConstant corresponding to the string in input
     * @param str A string image */
    public static Operator fromString(String str) {
        for (Operator op : Operator.values()) {
            if (op.image.equals(str)) return op;
        }
        return null;
    }


}
