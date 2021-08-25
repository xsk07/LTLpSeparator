package separator;

import formula.*;
import java.util.ArrayList;
import static formula.Operator.*;
import static separator.EliminationRules.*;

public class FormulaSeparator {

    /** Separates a formula into a conjunction of pure past, pure present and pure future formulas,
     *  applying the elimination rules of the Gabbay' Separation Theorem.
     *  @param f The formula to separate
     *  @return Returns the separated formula */
    public static Formula separate(Formula f) {
        if(f.isOperator()){
            OperatorFormula of = (OperatorFormula) f;
            if(of.isBinary()){
                if(of.getOperator() == SINCE) {
                    BinaryFormula sf = (BinaryFormula) of;
                    int nc = nestingCase(sf);
                    ArrayList<Formula> sfms;
                    switch (nc) {
                        case 1:
                            sfms = subformulas1(sf);
                            return elimination1(sfms);
                        case 2:
                            sfms = subformulas2(sf);
                            return elimination2(sfms);
                        case 3:
                            sfms = subformulas3(sf);
                            return elimination3(sfms);
                        case 4:
                            sfms = subformulas4(sf);
                            return f; // elimination4(sfms);
                        case 5:
                            sfms = subformulas57(sf);
                            return elimination5(sfms);
                        case 6:
                            sfms = subformulas6(sf);
                            return elimination6(sfms);
                        case 7:
                            sfms = subformulas57(sf);
                            return elimination7(sfms);
                        case 8:
                            sfms = subformulas8(sf);
                            return elimination8(sfms);
                        default: return f;
                    }
                }
                else return f; // missing UNTIL case to be inplemented in the previous if body
            }
            if(of.isUnary()) {
                UnaryFormula uf = (UnaryFormula) of;
                Formula nf = separate(uf.getOperand());
                uf.setOperand(nf);
                return uf;
            }
        }
        return f;
    }

    /** @return Returns the number of the nesting case of the formula.
     * If no nesting occurrences of U and S are present, returns 0 */
    public static int nestingCase(BinaryFormula f) {
        Operator fOp = f.getOperator();
        if(fOp == SINCE || fOp == UNTIL) {
            int lsc = leftSubtreeCase(f);
            int rsc = rightSubtreeCase(f);
            return switch (lsc) {
                case 1 -> switch (rsc) {
                    case 1 -> 5;
                    case 2 -> 7;
                    default -> 1;
                };
                case 2 -> switch (rsc) {
                    case 1 -> 6;
                    case 2 -> 8;
                    default -> 2;
                };
                default -> switch (rsc) {
                    case 1 -> 3;
                    case 2 -> 4;
                    default -> 0;
                };
            };
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have S or U as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }

    // pre: f.getOperator() == SINCE/UNTIL
    private static int leftSubtreeCase(BinaryFormula f) {
        int n = 0;
        Operator fOp = f.getOperator();
        Formula x = f.getLoperand();
        if(x.isOperator()){
            OperatorFormula ofX = (OperatorFormula) x;
            if(ofX.getOperator() == AND) {
                BinaryFormula andX = (BinaryFormula) ofX;
                Formula y = andX.getRoperand();
                if(y.isOperator()) {
                    OperatorFormula ofY = (OperatorFormula) y;
                    if(ofY.isOperator()){
                        if(ofY.getOperator() == fOp.getMirrorOperator()) {
                            n = 1;
                        }
                        if(ofY.getOperator() == NOT) {
                            UnaryFormula ufY = (UnaryFormula) ofY;
                            Formula z = ufY.getOperand();
                            if(z.isOperator()){
                                OperatorFormula ofZ = (OperatorFormula) z;
                                if(ofZ.getOperator() == fOp.getMirrorOperator()) {
                                    n = 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return n;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    private static int rightSubtreeCase(BinaryFormula f) {
        int n = 0;
        Operator fOp = f.getOperator();
        Formula x = f.getRoperand();
        if(x.isOperator()){
            OperatorFormula ofX = (OperatorFormula) x;
            if(ofX.getOperator() == OR) {
                BinaryFormula andX = (BinaryFormula) ofX;
                Formula y = andX.getRoperand();
                if(y.isOperator()) {
                    OperatorFormula ofY = (OperatorFormula) y;
                    if(ofY.isOperator()){
                        if(ofY.getOperator() == fOp.getMirrorOperator()) {
                            n = 1;
                        }
                        if(ofY.getOperator() == NOT) {
                            UnaryFormula ufY = (UnaryFormula) ofY;
                            Formula z = ufY.getOperand();
                            if(z.isOperator()){
                                OperatorFormula ofZ = (OperatorFormula) z;
                                if(ofZ.getOperator() == fOp.getMirrorOperator()) {
                                    n = 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return n;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    public static ArrayList<Formula> subformulas1(BinaryFormula f){
        ArrayList<Formula> al= new ArrayList<>();
        BinaryFormula lAnd =  (BinaryFormula) f.getLoperand();
        Formula a = lAnd.getLoperand();
        BinaryFormula lUntil = (BinaryFormula) lAnd.getRoperand();
        Formula uA = lUntil.getLoperand();
        Formula uB = lUntil.getRoperand();
        Formula q = f.getRoperand();
        al.add(a);
        al.add(uA);
        al.add(uB);
        al.add(q);
        return al;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    public static ArrayList<Formula> subformulas2(BinaryFormula f){
        ArrayList<Formula> al= new ArrayList<>();
        BinaryFormula lAnd =  (BinaryFormula) f.getLoperand();
        Formula a = lAnd.getLoperand();
        UnaryFormula lNot = (UnaryFormula) lAnd.getRoperand();
        BinaryFormula lUntil = (BinaryFormula) lNot.getOperand();
        Formula uA = lUntil.getLoperand();
        Formula uB = lUntil.getRoperand();
        Formula q = f.getRoperand();
        al.add(a);
        al.add(uA);
        al.add(uB);
        al.add(q);
        return al;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    public static ArrayList<Formula> subformulas3(BinaryFormula f){
        ArrayList<Formula> al= new ArrayList<>();
        Formula a = f.getLoperand();
        BinaryFormula rOr = (BinaryFormula) f.getRoperand();
        Formula q = rOr.getLoperand();
        BinaryFormula rUntil = (BinaryFormula) rOr.getRoperand();
        Formula uA = rUntil.getLoperand();
        Formula uB = rUntil.getRoperand();
        al.add(a);
        al.add(uA);
        al.add(uB);
        al.add(q);
        return al;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    public static ArrayList<Formula> subformulas4(BinaryFormula f){
        ArrayList<Formula> al= new ArrayList<>();
        Formula a = f.getLoperand();
        BinaryFormula rOr = (BinaryFormula) f.getRoperand();
        Formula q = rOr.getLoperand();
        UnaryFormula rNot = (UnaryFormula) rOr.getRoperand();
        BinaryFormula rUntil = (BinaryFormula) rNot.getOperand();
        Formula uA = rUntil.getLoperand();
        Formula uB = rUntil.getRoperand();
        al.add(a);
        al.add(uA);
        al.add(uB);
        al.add(q);
        return al;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    public static ArrayList<Formula> subformulas6(BinaryFormula f){
        ArrayList<Formula> al= new ArrayList<>();
        BinaryFormula lAnd = (BinaryFormula) f.getLoperand();
        Formula a = lAnd.getLoperand();
        BinaryFormula rOr = (BinaryFormula) f.getRoperand();
        Formula q = rOr.getLoperand();
        BinaryFormula rUntil = (BinaryFormula) rOr.getRoperand();
        Formula uA = rUntil.getLoperand();
        Formula uB = rUntil.getRoperand();
        al.add(a);
        al.add(uA);
        al.add(uB);
        al.add(q);
        return al;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    public static ArrayList<Formula> subformulas57(BinaryFormula f){
        ArrayList<Formula> al= new ArrayList<>();
        BinaryFormula lAnd =  (BinaryFormula) f.getLoperand();
        Formula a = lAnd.getLoperand();
        BinaryFormula lUntil = (BinaryFormula) lAnd.getRoperand();
        Formula uA = lUntil.getLoperand();
        Formula uB = lUntil.getRoperand();
        BinaryFormula rOr = (BinaryFormula) f.getRoperand();
        Formula q = rOr.getLoperand();
        al.add(a);
        al.add(uA);
        al.add(uB);
        al.add(q);
        return al;
    }

    // pre: f.getOperator() == SINCE/UNTIL
    public static ArrayList<Formula> subformulas8(BinaryFormula f){
        ArrayList<Formula> al= new ArrayList<>();
        BinaryFormula lAnd =  (BinaryFormula) f.getLoperand();
        Formula a = lAnd.getLoperand();
        UnaryFormula lNot = (UnaryFormula) lAnd.getRoperand();
        BinaryFormula lUntil = (BinaryFormula) lNot.getOperand();
        Formula uA = lUntil.getLoperand();
        Formula uB = lUntil.getRoperand();
        BinaryFormula rOr = (BinaryFormula) f.getRoperand();
        Formula q = rOr.getLoperand();
        al.add(a);
        al.add(uA);
        al.add(uB);
        al.add(q);
        return al;
    }

}
