package formula;

import static formula.Operator.SINCE;
import static formula.Operator.UNTIL;

public class Path {

    private OperatorFormula x;
    private Formula y;

    public Path(OperatorFormula fx, Formula fy) {
        if(!fx.isAncestorOf(fy)) throw new IllegalArgumentException(
                "fx must to be an ancestor of fy"
        );
        x = fx; y = fy;
    }

    public void setX(OperatorFormula z) { x = z; }

    public void setY(Formula z) { y = z; }

    public Formula getX() { return x; }

    public Formula getY() { return y; }

    @Override
    public String toString() {
        Formula z = y;
        StringBuilder sb = new StringBuilder();
        while(z != x) {
            sb.append(z.getImage() + ",");
            z = z.getParent();
        }
        sb.append(z.getImage());
        StringBuilder revSb = sb.reverse();
        return String.format("< %s >", revSb);
    }

    public int countOperatorOccurrences(Operator op) {
        int c = 0;
        Formula z = y;
        while(z != x) {
            if(z.isOperator(op)) c +=1;
            z = z.getParent();
        }
        if(z.isOperator(op)) c +=1;
        return c;
    }

    /** The number of changes inside the path. */
    public int getM() {
        int c = 0;
        Operator op = null;
        Formula z = y;
        while(z != x) {
            if(op == null) {
                if(z instanceof BinaryFormula bz) {
                    boolean isUntilOrSince = bz.isOperator(UNTIL) || bz.isOperator(SINCE);
                    if(isUntilOrSince) op = bz.getOperator();
                }
            }
            else {
                if(z instanceof BinaryFormula bz && z.isOperator(op.getMirrorOperator())) {
                    c += 1; op = bz.getOperator();
                }
            }
            z = z.getParent();
        }
        if(op != null && z instanceof BinaryFormula bz && z.isOperator(op.getMirrorOperator())) c += 1;
        return c;
    }

    public Junction getTopJunction() {
        Junction j = null;
        BinaryFormula r = null;
        Formula z = y;
        while(z != x) {
            if(r == null) {
                if(z instanceof BinaryFormula bz) {
                    boolean isUntilOrSince = bz.isOperator(UNTIL) || bz.isOperator(SINCE);
                    if(isUntilOrSince) r = bz;
                }
            }
            else {
                if(z instanceof BinaryFormula bz && z.isOperator(r.getOperator().getMirrorOperator())) {
                    j = new Junction(bz, r);
                    r = bz;
                }
            }
            z = z.getParent();
        }
        if(r != null && z instanceof BinaryFormula bz && bz.isOperator(r.getOperator().getMirrorOperator())) {
            j = new Junction(bz, r);
        }
        return j;
    }





}
