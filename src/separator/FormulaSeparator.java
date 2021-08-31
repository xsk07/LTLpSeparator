package separator;

import formula.*;

import java.util.ArrayList;

import static formula.Operator.*;
import static separator.EliminationRules.*;

public class FormulaSeparator {

    public static Formula eliminate(Formula f){

        Formula before = f;
        Formula after = applyEliminations(f);
        while (!before.equals(after)){
            before = after;
            after = applyEliminations(after);
        }
        return after;
    }


    /** Separates a formula into a conjunction of pure past, pure present and pure future formulas,
     *  applying the elimination rules of the Gabbay' Separation Theorem.
     *  @param f The formula to applyEliminations
     *  @return Returns the separated formula
     **/
    public static Formula applyEliminations(Formula f) {
        if(f.isOperator()){
            OperatorFormula of = (OperatorFormula) f;
            if(of.isBinary()){
                BinaryFormula bf = (BinaryFormula) of;
                Operator op_f = bf.getOperator();
                if(op_f == SINCE || op_f == UNTIL) {
                    int nc = nestingCase(bf);
                    ArrayList<Formula> sfms;
                    switch (nc) {
                        case 1 -> {
                            separateOperands(bf);
                            sfms = subformulas1(bf);
                            System.out.println("Elimination1");
                            return elimination1(sfms, op_f);
                        }
                        case 2 -> {
                            separateOperands(bf);
                            sfms = subformulas2(bf);
                            System.out.println("Elimination2");
                            return elimination2(sfms, op_f);
                        }
                        case 3 -> {
                            separateOperands(bf);
                            sfms = subformulas3(bf);
                            System.out.println("Elimination3");
                            return elimination3(sfms, op_f);
                        }
                        case 4 -> {
                            separateOperands(bf);
                            sfms = subformulas4(bf);
                            System.out.println("Elimination4");
                            return elimination4_v1(sfms, op_f);
                        }
                        case 5 -> {
                            separateOperands(bf);
                            sfms = subformulas57(bf);
                            System.out.println("Elimination5");
                            return elimination5(sfms, op_f);
                        }
                        case 6 -> {
                            separateOperands(bf);
                            sfms = subformulas6(bf);
                            System.out.println("Elimination6");
                            return elimination6(sfms, op_f);
                        }
                        case 7 -> {
                            separateOperands(bf);
                            sfms = subformulas57(bf);
                            System.out.println("Elimination7");
                            return elimination7(sfms, op_f);
                        }
                        case 8 -> {
                            separateOperands(bf);
                            sfms = subformulas8(bf);
                            System.out.println("Elimination8");
                            return elimination8(sfms, op_f);
                        }
                        default -> {
                            separateOperands(bf);
                            //if(nestingCase(bf) != 0) return applyEliminations(bf);
                            return bf;
                        }
                    }
                }
                else {
                    separateOperands(bf);
                    return bf;
                }
            }
            if(of.isUnary()) {
                UnaryFormula uf = (UnaryFormula) of;
                separateOperand(uf);
                return uf;
            }
        }
        return f;
    }

    /** Separates the left and the right operands of the formula.
     * @param f The binary formula on which will be applied the separation on its the operands */
    public static void separateOperands(BinaryFormula f){
        Formula lc = f.getLoperand();
        Formula rc = f.getRoperand();
        f.setLoperand(applyEliminations(lc));
        f.setRoperand(applyEliminations(rc));
    }
    /** Separates the operand of the formula.
     * @param f The unary formula on which will be applied the separation on its the operand */
    public static void separateOperand(UnaryFormula f){
        f.setOperand(applyEliminations(f.getOperand()));
    }

    /** @return Returns the number of the nesting case of the formula.
     * If the formula does not correspond to any nesting case of the eliminations returns 0 */
    public static int nestingCase(BinaryFormula f) {
        Operator fOp = f.getOperator();
        if(fOp == SINCE || fOp == UNTIL) {
            int lsc = leftSubtreeCase(f);
            int rsc = rightSubtreeCase(f);
            switch (lsc) {
                case 0:
                    switch (rsc) {
                        case 1:
                            return 3;
                        case 2:
                            return 4;
                        default:
                            return 0;
                    }
                case 1:
                    switch (rsc) {
                        case 1: {
                            ArrayList<Formula> ltsf = subformulas1(f);
                            ArrayList<Formula> rtsf = subformulas3(f);
                            if(ltsf.get(1).equals(rtsf.get(1)) && ltsf.get(2).equals(rtsf.get(2))) return 5;
                        }
                        case 2: {
                            ArrayList<Formula> ltsf = subformulas1(f);
                            ArrayList<Formula> rtsf = subformulas4(f);
                            if(ltsf.get(1).equals(rtsf.get(1)) && ltsf.get(2).equals(rtsf.get(2))) return 7;
                        }
                        default: return 1;
                    }
                case 2:
                    switch (rsc) {
                        case 1: {
                            ArrayList<Formula> ltsf = subformulas2(f);
                            ArrayList<Formula> rtsf = subformulas3(f);
                            if(ltsf.get(1).equals(rtsf.get(1)) && ltsf.get(2).equals(rtsf.get(2))) return 6;
                        }
                        case 2: {
                            ArrayList<Formula> ltsf = subformulas2(f);
                            ArrayList<Formula> rtsf = subformulas4(f);
                            if(ltsf.get(1).equals(rtsf.get(1)) && ltsf.get(2).equals(rtsf.get(2))) return 8;
                        }
                        default: return 2;
                    }
                default: return 0;
            }
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have S or U as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }

    // pre: f.getOperator() == SINCE/UNTIL
    /** Recognizes the left subtree case/pattern.
     * @return Returns 1 if the left child of the formula is of the form: (a&(AUB))Sq
     * Returns 2 if the left child of the formula is of the form: (a&!(AUB))Sq
     * Returns 0 elsewhere */
    private static int leftSubtreeCase(BinaryFormula f) {
        int n = 0;
        Operator fOp = f.getOperator(); // the top operator of the formula f
        Formula x = f.getLoperand(); // the left operand of the formula f
        if(x.isOperator()){
            OperatorFormula ofX = (OperatorFormula) x;
            // if the left operand is an AND
            if(ofX.getOperator() == AND ) {
                BinaryFormula andX = (BinaryFormula) ofX;

                /* since the AND is a commutative operator it's possible to have the reversed
                situation where the binary temporal operator is on the left rather than on the
                right. For simplicity in this situation the two operands will be swapped and
                led the two situations to a unique case. */
                Formula lcAND = andX.getLoperand();
                if(lcAND.isOperator()){
                    OperatorFormula ofLcAND = (OperatorFormula) lcAND;
                    if(ofLcAND.getOperator() == fOp.getMirrorOperator()) {
                        andX.swapChildren();
                    }
                    else if (ofLcAND.getOperator() == NOT){
                        UnaryFormula uOfLcAND = (UnaryFormula) ofLcAND;
                        Formula uOfLcAndOperand = uOfLcAND.getOperand();
                        if(uOfLcAndOperand.isOperator()) {
                            OperatorFormula OpUOfLcAndOperand = (OperatorFormula) uOfLcAndOperand;
                            if(OpUOfLcAndOperand.getOperator() == fOp.getMirrorOperator()){
                                andX.swapChildren();
                            }
                        }
                    }
                }

                Formula y = andX.getRoperand();
                // andX.getLoperand().isAtomic() &&
                if(y.isOperator()) {
                    OperatorFormula ofY = (OperatorFormula) y;
                    if(ofY.isOperator()){
                        if(ofY.getOperator() == fOp.getMirrorOperator()) {
                            return  1;
                        }
                        if(ofY.getOperator() == NOT) {
                            UnaryFormula ufY = (UnaryFormula) ofY;
                            Formula z = ufY.getOperand();
                            if(z.isOperator()){
                                OperatorFormula ofZ = (OperatorFormula) z;
                                if(ofZ.getOperator() == fOp.getMirrorOperator()) {
                                    return  2;
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
    /** Recognizes the right subtree case/pattern.
     * @return Returns 1 if the right child of the formula is of the form: (a|(AUB))Sq
     * Returns 2 if the right child of the formula is of the form: (a|!(AUB))Sq
     * Returns 0 elsewhere */
    private static int rightSubtreeCase(BinaryFormula f) {
        int n = 0;
        Operator fOp = f.getOperator();
        Formula x = f.getRoperand();
        if(x.isOperator()){
            OperatorFormula ofX = (OperatorFormula) x;
            if(ofX.getOperator() == OR) {
                BinaryFormula orX = (BinaryFormula) ofX;

                /* since the OR is a commutative operator it's possible to have the reversed
                situation where the binary temporal operator is on the left rather than on the
                right. For simplicity in this situation the two operands will be swapped and
                led the two situations to a unique case. */
                Formula lcOR = orX.getLoperand();
                if(lcOR.isOperator()){
                    OperatorFormula ofLcOR = (OperatorFormula) lcOR;
                    if(ofLcOR.getOperator() == fOp.getMirrorOperator()) {
                        orX.swapChildren();
                    }
                    else if (ofLcOR.getOperator() == NOT){
                        UnaryFormula uOfLcOR = (UnaryFormula) ofLcOR;
                        Formula uOfLcOrOperand = uOfLcOR.getOperand();
                        if(uOfLcOrOperand.isOperator()) {
                            OperatorFormula OpUOfLcOrOperand = (OperatorFormula) uOfLcOrOperand;
                            if(OpUOfLcOrOperand.getOperator() == fOp.getMirrorOperator()){
                                orX.swapChildren();
                            }
                        }
                    }
                }

                Formula y = orX.getRoperand();
                // orX.getLoperand().isAtomic() &&
                if(y.isOperator()) {
                    OperatorFormula ofY = (OperatorFormula) y;
                    if(ofY.isOperator()){
                        if(ofY.getOperator() == fOp.getMirrorOperator()) {
                            return 1;
                        }
                        if(ofY.getOperator() == NOT) {
                            UnaryFormula ufY = (UnaryFormula) ofY;
                            Formula z = ufY.getOperand();
                            if(z.isOperator()){
                                OperatorFormula ofZ = (OperatorFormula) z;
                                if(ofZ.getOperator() == fOp.getMirrorOperator()) {
                                    return 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return n;
    }

    /** @return Returns the array list of the subformulas (q,A,B,a) which will be used in the Elimination1 */
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

    /** @return Returns the array list of the subformulas (q,A,B,a) which will be used in the Elimination2 */
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

    /** @return Returns the array list of the subformulas (q,A,B,a) which will be used in the Elimination3 */
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

    /** @return Returns the array list of the subformulas (q,A,B,a) which will be used in the Elimination4 */
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


    /** @return Returns the array list of the subformulas (q,A,B,a) which will be used in the Elimination5
     * and Elimination7 */
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

    /** @return Returns the array list of the subformulas (q,A,B,a) which will be used in the Elimination6 */
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

    /** @return Returns the array list of the subformulas which will be used in the Elimination8 */
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
