package separator.eliminator;

import formula.*;
import java.util.ArrayList;
import formula.Junction;
import static formula.Operator.*;
import static formula.BinaryFormula.newConjunction;
import static formula.BinaryFormula.newDisjunction;
import static separator.FormulaSeparator.junctionCase;
import static separator.eliminator.EliminationRules.*;


public class FormulaEliminator {

    /** Applies the correct elimination for the binary formula got in input.
     *  @return returns the formula after being applied the corresponding elimination,
     *  if no elimination can be applied returns the same input formula */
    public static OperatorFormula applyElimination(Junction lj, Junction rj) {

        BinaryFormula x = null;
        if(lj != null) x = (BinaryFormula) lj.getX();
        else if(rj != null) x = (BinaryFormula) rj.getX();

        if(lj != null && rj != null) {
            BinaryFormula lx = (BinaryFormula) lj.getX();
            BinaryFormula rx = (BinaryFormula) rj.getX();
            if(!(lx == rx)) {
                throw new IllegalArgumentException (
                        "The left and the right junctions must have the same x node"
                );
            }

        }

        assert x != null;
        if(!(x.isOperator(SINCE) || x.isOperator(UNTIL))) {
            throw new IllegalArgumentException (
                    String.format(
                            "The operator of the formula must be S or U but is %s",
                            x.getOperator()
                    )
            );
        }

        int nc = eliminationNumber(lj, rj);
        Formula[] sfms = getSubformulas(lj, rj, nc);
        Operator op = x.getOperator();
        return switch (nc) {
            case 1 -> elimination1(sfms,op);
            case 2 -> elimination2(sfms,op);
            case 3 -> elimination3(sfms,op);
            case 4 -> elimination4(sfms,op);
            case 5 -> elimination5(sfms,op);
            case 6 -> elimination6(sfms,op);
            case 7 -> elimination7(sfms,op);
            case 8 -> elimination8(sfms,op);
            default -> x;
        };
    }

    /** @return Returns the number of the elimination to apply.
     * If the formula does not correspond to any nesting case of the eliminations returns 0. */
    public static int eliminationNumber(Junction lj, Junction rj) {
        if(junctionCase(lj) == 0) { /* if there is no left junction */
            if(junctionCase(rj) == 1) return 2; // S(a, q | U(A,B))
            if(junctionCase(rj) == 2) return 4; // S(a, q | !U(A,B))
        } else { /* if there is a left junction */
            if(junctionCase(lj) == 1) {
                if(junctionCase(rj) == 0) return 1; // S(a & U(A,B), q)
                if(junctionCase(rj) == 1) {
                    BinaryFormula ly = (BinaryFormula) lj.getY();
                    BinaryFormula ry = (BinaryFormula) rj.getY();
                    Formula lA = ly.getLoperand();
                    Formula rA = ry.getLoperand();
                    Formula lB = ly.getRoperand();
                    Formula rB = ry.getRoperand();
                    if(lA.equalTo(rA) && lB.equalTo(rB)) return 5; // S(a & U(A,B), q | U(A,B))
                }
                if(junctionCase(rj) == 2) {
                    BinaryFormula ly = (BinaryFormula) lj.getY();
                    BinaryFormula ry = (BinaryFormula) rj.getY();
                    Formula lA = ly.getLoperand();
                    Formula rA = ry.getLoperand();
                    Formula lB = ly.getRoperand();
                    Formula rB = ry.getRoperand();
                    if(lA.equalTo(rA) && lB.equalTo(rB)) return 7; // S(a & U(A,B), q | !U(A,B))
                }
            }
            if(junctionCase(lj) == 2) {
                if(junctionCase(rj) == 0) return 3; // S(a & !U(A,B), q)
                if(junctionCase(rj) == 1) {
                    BinaryFormula ly = (BinaryFormula) lj.getY();
                    BinaryFormula ry = (BinaryFormula) rj.getY();
                    Formula lA = ly.getLoperand();
                    Formula rA = ry.getLoperand();
                    Formula lB = ly.getRoperand();
                    Formula rB = ry.getRoperand();
                    if(lA.equalTo(rA) && lB.equalTo(rB)) return 6; // S(a & !U(A,B), q | U(A,B))
                }
                if(junctionCase(rj) == 2) {
                    BinaryFormula ly = (BinaryFormula) lj.getY();
                    BinaryFormula ry = (BinaryFormula) rj.getY();
                    Formula lA = ly.getLoperand();
                    Formula rA = ry.getLoperand();
                    Formula lB = ly.getRoperand();
                    Formula rB = ry.getRoperand();
                    if(lA.equalTo(rA) && lB.equalTo(rB)) return 8; // S(a & !U(A,B), q | !U(A,B))
                }
            }
        }
        return 0; // S(C,D)
    }

    /** Returns an array containing the sub-formulas of the formula f.
     * [0] == a, [1] == A, [2] == B, [3] == q */
    protected static Formula[] getSubformulas(Junction lj, Junction rj, int c){
        return switch(c) {
            case 1 -> subformulas1(lj);
            case 2 -> subformulas2(rj);
            case 3 -> subformulas3(lj);
            case 4 -> subformulas4(rj);
            case 5 -> subformulas5(lj, rj);
            case 6 -> subformulas6(lj, rj);
            case 7 -> subformulas7(lj, rj);
            case 8 -> subformulas8(lj, rj);
            default -> new Formula[4]; // exception
        };
    }

    /** S(a & U(A,B), q) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[] subformulas1(Junction lj){
        BinaryFormula x = (BinaryFormula) lj.getX();
        BinaryFormula y = (BinaryFormula) lj.getY();
        BinaryFormula lAnd = (BinaryFormula) x.getLoperand();
        ArrayList<Formula> as = lAnd.getCombinationOperands();
        as.removeIf(z -> z == y);

        Formula a = as.iterator().next();
        if(as.size() > 1) a = newConjunction(as);
        lAnd.replaceFormula(new BinaryFormula(AND, a, y));
        return new Formula[] {
                a, // a
                y.getLoperand(), // A
                y.getRoperand(), // B
                x.getRoperand()  // q
        };
    }

    /** S(a, q | U(A,B)) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[] subformulas2(Junction rj){
        BinaryFormula x = (BinaryFormula) rj.getX();
        BinaryFormula y = (BinaryFormula) rj.getY();
        BinaryFormula rOr = (BinaryFormula) x.getRoperand();
        ArrayList<Formula> qs = rOr.getCombinationOperands();
        qs.removeIf(z -> z == y);

        Formula q = qs.iterator().next();
        if(qs.size() > 1) q = newDisjunction(qs);
        rOr.replaceFormula(new BinaryFormula(OR, q, y));
        return new Formula[] {
                x.getLoperand(), // a
                y.getLoperand(), // A
                y.getRoperand(), // B
                q  // q
        };
    }

    /** S(a & !U(A,B), q) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[] subformulas3(Junction lj){
        BinaryFormula x = (BinaryFormula) lj.getX();
        BinaryFormula y = (BinaryFormula) lj.getY();
        BinaryFormula lAnd = (BinaryFormula) x.getLoperand();
        ArrayList<Formula> as = lAnd.getCombinationOperands();
        as.removeIf(z -> z == y.getParent());

        Formula a = as.iterator().next();
        if(as.size() > 1) a = newConjunction(as);
        lAnd.replaceFormula(new BinaryFormula(AND, a, y.getParent()));
        return new Formula[] {
                a, // a
                y.getLoperand(), // A
                y.getRoperand(), // B
                x.getRoperand()  // q
        };
    }

    /** S(a, q | !U(A,B)) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[]  subformulas4(Junction rj){
        BinaryFormula x = (BinaryFormula) rj.getX();
        BinaryFormula y = (BinaryFormula) rj.getY();
        BinaryFormula rOr = (BinaryFormula) x.getRoperand();
        ArrayList<Formula> qs = rOr.getCombinationOperands();
        qs.removeIf(z -> z == y.getParent());

        Formula q = qs.iterator().next();
        if(qs.size() > 1) q = newDisjunction(qs);
        rOr.replaceFormula(new BinaryFormula(OR, q, y.getParent()));
        return new Formula[] {
                x.getLoperand(), // a
                y.getLoperand(), // A
                y.getRoperand(), // B
                q  // q
        };
    }

    /** S(a & U(A,B), q | U(A,B)) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[] subformulas5(Junction lj, Junction rj){
        BinaryFormula x = (BinaryFormula) lj.getX();

        BinaryFormula ly = (BinaryFormula) lj.getY();
        BinaryFormula lAnd = (BinaryFormula) x.getLoperand();
        ArrayList<Formula> as = lAnd.getCombinationOperands();
        as.removeIf(z -> z == ly);

        Formula a = as.iterator().next();
        if(as.size() > 1) a = newConjunction(as);
        lAnd.replaceFormula(new BinaryFormula(AND, a, ly));

        BinaryFormula ry = (BinaryFormula) rj.getY();
        BinaryFormula rOr = (BinaryFormula) x.getRoperand();
        ArrayList<Formula> qs = rOr.getCombinationOperands();
        qs.removeIf(z -> z == ry);

        Formula q = qs.iterator().next();
        if(qs.size() > 1) q = newDisjunction(qs);
        rOr.replaceFormula(new BinaryFormula(OR, q, ry));

        return new Formula[] {
                a, // a
                ly.getLoperand(), // A
                ly.getRoperand(), // B
                q  // q
        };
    }

    /** S(a & !U(A,B), q | U(A,B)) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[] subformulas6(Junction lj, Junction rj) {
        BinaryFormula x = (BinaryFormula) lj.getX();

        BinaryFormula ly = (BinaryFormula) lj.getY();
        BinaryFormula lAnd = (BinaryFormula) x.getLoperand();
        ArrayList<Formula> as = lAnd.getCombinationOperands();
        as.removeIf(z -> z == ly.getParent());

        Formula a = as.iterator().next();
        if(as.size() > 1) a = newConjunction(as);
        lAnd.replaceFormula(new BinaryFormula(AND, a, ly.getParent()));

        BinaryFormula ry = (BinaryFormula) rj.getY();
        BinaryFormula rOr = (BinaryFormula) x.getRoperand();
        ArrayList<Formula> qs = rOr.getCombinationOperands();
        qs.removeIf(z -> z == ry);

        Formula q = qs.iterator().next();
        if(qs.size() > 1) q = newDisjunction(qs);
        rOr.replaceFormula(new BinaryFormula(OR, q, ry));

        return new Formula[] {
                a, // a
                ry.getLoperand(), // A
                ry.getRoperand(), // B
                q  // q
        };
    }

    /** S(a & U(A,B), q | !U(A,B)) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[] subformulas7(Junction lj, Junction rj) {
        BinaryFormula x = (BinaryFormula) lj.getX();

        BinaryFormula ly = (BinaryFormula) lj.getY();
        BinaryFormula lAnd = (BinaryFormula) x.getLoperand();
        ArrayList<Formula> as = lAnd.getCombinationOperands();
        as.removeIf(z -> z == ly);

        Formula a = as.iterator().next();
        if(as.size() > 1) a = newConjunction(as);
        lAnd.replaceFormula(new BinaryFormula(AND, a, ly));

        BinaryFormula ry = (BinaryFormula) rj.getY();
        BinaryFormula rOr = (BinaryFormula) x.getRoperand();
        ArrayList<Formula> qs = rOr.getCombinationOperands();
        qs.removeIf(z -> z == ry.getParent());

        Formula q = qs.iterator().next();
        if(qs.size() > 1) q = newDisjunction(qs);
        rOr.replaceFormula(new BinaryFormula(OR, q, ry.getParent()));

        return new Formula[] {
                a, // a
                ly.getLoperand(), // A
                ly.getRoperand(), // B
                q  // q
        };
    }

    /** S(a & !U(A,B), q | !U(A,B)) */
    // pre: f.getOperator() == SINCE/UNTIL
    protected static Formula[] subformulas8(Junction lj, Junction rj) {
        BinaryFormula x = (BinaryFormula) lj.getX();

        BinaryFormula ly = (BinaryFormula) lj.getY();
        BinaryFormula lAnd = (BinaryFormula) x.getLoperand();
        ArrayList<Formula> as = lAnd.getCombinationOperands();
        as.removeIf(z -> z == ly.getParent());

        Formula a = as.iterator().next();
        if(as.size() > 1) a = newConjunction(as);
        lAnd.replaceFormula(new BinaryFormula(AND, a, ly.getParent()));

        BinaryFormula ry = (BinaryFormula) rj.getY();
        BinaryFormula rOr = (BinaryFormula) x.getRoperand();
        ArrayList<Formula> qs = rOr.getCombinationOperands();
        qs.removeIf(z -> z == ry.getParent());

        Formula q = qs.iterator().next();
        if(qs.size() > 1) q = newDisjunction(qs);
        rOr.replaceFormula(new BinaryFormula(OR, q, ry.getParent()));
        return new Formula[] {
                a, // a
                ly.getLoperand(), // A
                ly.getRoperand(), // B
                q  // q
        };
    }


}
