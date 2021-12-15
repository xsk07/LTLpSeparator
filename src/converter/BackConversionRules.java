package converter;

import formula.*;
import static formula.Operator.*;

public abstract class BackConversionRules {

    public static boolean patternO(BinaryFormula f) {
        if(f.isOperator(SINCE)) {
            Formula rc = f.getRoperand();
            if(rc instanceof AtomicFormula ar) return ar.isTrue();
        }
        return false;
    }

    /** Rewriting rule: S(q, true)  =>* Oq  */
    public static UnaryFormula backO(BinaryFormula f) {
        if(!patternO(f)) throw new IllegalArgumentException(
                "The formula must be of the form S(q, true)"
        );
        return new UnaryFormula(
                ONCE, // O
                f.getLoperand() // q
        );
    }

    public static boolean patternH(BinaryFormula f) {
        if(f.isOperator(SINCE)) {
            Formula lc = f.getLoperand();
            Formula rc = f.getRoperand();
            Formula p = f.getParent();
            boolean parentIsNot = p.isOperator(NOT);
            boolean leftChildIsNot = lc.isOperator(NOT);
            boolean rightChildIsTrue = rc instanceof AtomicFormula ar && ar.isTrue();
            return parentIsNot && leftChildIsNot && rightChildIsTrue;
        }
        return false;
    }

    /** !S(!q, true) =>* Hq */
    public static UnaryFormula backH(BinaryFormula f) {
        if(!patternH(f)) throw new IllegalArgumentException(
                "The formula must be of the form !S(!q, true)"
        );
        UnaryFormula lc = (UnaryFormula) f.getLoperand();
        Formula q = lc.getOperand();
        return new UnaryFormula(HIST, q);
    }

    public static boolean patternY(BinaryFormula f) {
        if(f.isOperator(SINCE)) {
            Formula rc = f.getRoperand();
            if(rc instanceof AtomicFormula ar) return ar.isFalse();
        }
        return false;
    }

    /** S(q, false) =>* Yq */
    public static UnaryFormula backY(BinaryFormula f) {
        if(!patternY(f)) throw new IllegalArgumentException(
                "The formula must be of the form S(q, false)"
        );
        return new UnaryFormula(
                YEST, // Y
                f.getLoperand() // q
        );
    }

    public static boolean patternF(BinaryFormula f) {
        if(f.isOperator(UNTIL)) {
            Formula rc = f.getRoperand();
            if(rc instanceof AtomicFormula ar) return ar.isTrue();
        }
        return false;
    }

    /** U(q, true) =>* Fq */
    public static UnaryFormula backF(BinaryFormula f) {
        if(!patternF(f)) throw new IllegalArgumentException(
                "The formula must be of the form U(q, true)"
        );
        return new UnaryFormula(
                FIN, // F
                f.getLoperand() // q
        );
    }

    public static boolean patternX(BinaryFormula f) {
        if(f.isOperator(UNTIL)) {
            Formula rc = f.getRoperand();
            if(rc instanceof AtomicFormula ar) return ar.isFalse();
        }
        return false;
    }

    /** U(q, false) =>* Xq */
    public static UnaryFormula backX(BinaryFormula f) {
        if(!patternX(f)) throw new IllegalArgumentException(
                "The formula must be of the form U(q, false)"
        );
        return new UnaryFormula(
                NEXT, // X
                f.getLoperand() // q
        );
    }

    public static boolean patternG(BinaryFormula f) {
        if(f.isOperator(UNTIL)) {
            Formula lc = f.getLoperand();
            Formula rc = f.getRoperand();
            Formula p = f.getParent();
            boolean parentIsNot = p.isOperator(NOT);
            boolean leftChildIsNot = lc.isOperator(NOT);
            boolean rightChildIsTrue = rc instanceof AtomicFormula ar && ar.isTrue();
            return parentIsNot && leftChildIsNot && rightChildIsTrue;
        }
        return false;
    }

    /** !U(!q, true) =>* Gq */
    public static UnaryFormula backG(BinaryFormula f) {
        if(!patternG(f)) throw new IllegalArgumentException(
                "The formula must be of the form !U(!q, true)"
        );
        UnaryFormula lc = (UnaryFormula) f.getLoperand();
        Formula q = lc.getOperand();
        return new UnaryFormula(GLOB, q);
    }

    public static boolean patternW(BinaryFormula f) {
        if(f.isOperator(OR)) {
            Formula lc = f.getLoperand();
            Formula rc = f.getRoperand();
            BinaryFormula u;
            UnaryFormula g;
            boolean lcG_and_rcU = lc.isOperator(GLOB) && rc.isOperator(UNTIL);
            boolean lcU_and_rcG = lc.isOperator(UNTIL) && rc.isOperator(GLOB);
            if(lcU_and_rcG || lcG_and_rcU) {
                // U(p,q) | Gq
                if(lc.isOperator(UNTIL) && rc.isOperator(GLOB)) {
                    u = (BinaryFormula) lc;
                    g = (UnaryFormula) rc;
                }
                // Gq | U(p,q)
                // lc.isOperator(GLOB) && rc.isOperator(UNTIL)
                else {
                    g = (UnaryFormula) lc;
                    u = (BinaryFormula) rc;
                }
                Formula gChild = g.getOperand();
                Formula q = u.getRoperand();
                return gChild.equalTo(q);
            }
        }
        return false;
    }

    /** (U(p,q) | Gq) =>* W(p,q) */
    public static BinaryFormula backW(BinaryFormula f) {

        if(!patternW(f)) throw new IllegalArgumentException(
                "The formula must be of the form U(p,q) | Gq"
        );

        Formula lc = f.getLoperand();
        Formula rc = f.getRoperand();
        BinaryFormula u;
        // U(p,q) | Gq
        boolean lcU_and_rcG = lc.isOperator(UNTIL) && rc.isOperator(GLOB);
        if(lcU_and_rcG) u = (BinaryFormula) lc;
        // Gq | U(p,q)
        // if(lc.isOperator(GLOB) && rc.isOperator(UNTIL))
        else u = (BinaryFormula) rc;
        return new BinaryFormula(UNLESS, u.getRoperand(), u.getLoperand());
    }

}
