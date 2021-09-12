package separator;

import formula.*;
import java.util.ArrayList;
import static formula.BooleanRules.distributionRule;
import static formula.Operator.*;
import static separator.EliminationRules.*;
import static formula.BooleanRules.deMorganLaw;

public class FormulaSeparator {

    public static Formula separate(Formula f) {
        Formula before = f.deepCopy();
        Formula after = applyEliminations(f);
        while (!after.equals(before)){
            before = after.deepCopy();
            after = applyEliminations(before);
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
                    separateOperands(bf);
                    setupFormulaTree(bf);
                    int nc = nestingCase(bf);
                    ArrayList<Formula> sfms;
                    switch (nc) {
                        case 1 -> {
                            sfms = subformulas1(bf);
                            System.out.println("Elimination1");
                            return elimination1(sfms, op_f);
                        }
                        case 2 -> {
                            sfms = subformulas2(bf);
                            System.out.println("Elimination2");
                            return elimination2(sfms, op_f);
                        }
                        case 3 -> {
                            sfms = subformulas3(bf);
                            System.out.println("Elimination3");
                            return elimination3(sfms, op_f);
                        }
                        case 4 -> {
                            sfms = subformulas4(bf);
                            System.out.println("Elimination4");
                            return elimination4_v1(sfms, op_f);
                        }
                        case 5 -> {
                            sfms = subformulas57(bf);
                            System.out.println("Elimination5");
                            return elimination5(sfms, op_f);
                        }
                        case 6 -> {
                            sfms = subformulas6(bf);
                            System.out.println("Elimination6");
                            return elimination6(sfms, op_f);
                        }
                        case 7 -> {
                            sfms = subformulas57(bf);
                            System.out.println("Elimination7");
                            return elimination7(sfms, op_f);
                        }
                        case 8 -> {
                            sfms = subformulas8(bf);
                            System.out.println("Elimination8");
                            return elimination8(sfms, op_f);
                        }
                        default -> {
                            separateOperands(bf);
                            if(nestingCase(bf) != 0) {
                                return applyEliminations(bf);
                            }
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
                if(uf.isOperator(NOT) && uf.getOperand().isOperator(NOT)){
                    UnaryFormula op_uf = (UnaryFormula) uf.getOperand();
                    return applyEliminations(op_uf.getOperand());
                }
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
                    return switch (rsc) {
                        case 1 -> 3;
                        case 2 -> 4;
                        default -> 0;
                    };
                case 1:
                    switch (rsc) {
                        case 1: {
                            ArrayList<Formula> ltsf = subformulas1(f);
                            ArrayList<Formula> rtsf = subformulas3(f);
                            if(
                                    ltsf.get(1).equals(rtsf.get(1))
                                    && ltsf.get(2).equals(rtsf.get(2))
                            ) return 5;
                        }
                        case 2: {
                            ArrayList<Formula> ltsf = subformulas1(f);
                            ArrayList<Formula> rtsf = subformulas4(f);
                            if(
                                    ltsf.get(1).equals(rtsf.get(1))
                                    && ltsf.get(2).equals(rtsf.get(2))
                            ) return 7;
                        }
                        default: return 1;
                    }
                case 2:
                    switch (rsc) {
                        case 1: {
                            ArrayList<Formula> ltsf = subformulas2(f);
                            ArrayList<Formula> rtsf = subformulas3(f);
                            if(
                                    ltsf.get(1).equals(rtsf.get(1))
                                    && ltsf.get(2).equals(rtsf.get(2))
                            ) return 6;
                        }
                        case 2: {
                            ArrayList<Formula> ltsf = subformulas2(f);
                            ArrayList<Formula> rtsf = subformulas4(f);
                            if(
                                    ltsf.get(1).equals(rtsf.get(1))
                                    && ltsf.get(2).equals(rtsf.get(2))
                            ) return 8;
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

            if(ofX.getOperator() == AND ) {

                BinaryFormula andX = (BinaryFormula) ofX;

                Formula y = andX.getRoperand();
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

            // if the right operand is an OR
            if(ofX.getOperator().equals(OR)) {

                BinaryFormula orX = (BinaryFormula) ofX;

                Formula y = orX.getRoperand();
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

    private static void setupFormulaTree(BinaryFormula f) {
        Formula lc = f.getLoperand();
        Formula rc = f.getRoperand();

        /*if the operator of the left child of f is an AND then */
        if(lc.isOperator(AND)) {

            /* if the mirror operator is not found inside the left child of f then */
            if(operatorChainSearch((BinaryFormula) lc, f.getOperator().getMirrorOperator()) == null){

                /* if is found a formula whose operator is NOT and its operand is a formula with
                * the mirror operator of f then switch it with the right child of f */
                UnaryFormula  notInAndChain = (UnaryFormula) operatorChainSearch((BinaryFormula) lc, NOT);
                if(notInAndChain != null){
                    if(notInAndChain.getOperand().isOperator(f.getOperator().getMirrorOperator())){
                        rearrangeInnerFormula((BinaryFormula) lc, notInAndChain);

                    }
                }
                else {
                    BinaryFormula orInAndChain = (BinaryFormula) operatorChainSearch((BinaryFormula) lc, OR);
                    if(orInAndChain != null){
                        if(operatorChainSearch(orInAndChain, f.getOperator().getMirrorOperator()) != null){
                            setupOperatorChain(
                                    orInAndChain,
                                    f.getOperator().getMirrorOperator()
                            );
                            setupOperatorChain((BinaryFormula) lc, OR);
                            f.setLoperand(distributionRule((BinaryFormula) lc));
                        }
                    }
                }
            }
            /* if was found the mirror operator inside the left child of f then
            * rearrange the operator chain */
            else {
                setupOperatorChain(
                        (BinaryFormula) lc,
                        f.getOperator().getMirrorOperator()
                );
            }
        }

        if(rc.isOperator(OR)) {

            if(operatorChainSearch((BinaryFormula) rc, f.getOperator().getMirrorOperator()) == null) {

                /* if is found a formula whose operator is NOT and its operand is a formula with
                 * the mirror operator of f then switch it with the right child of f */
                UnaryFormula  notInOrChain = (UnaryFormula) operatorChainSearch((BinaryFormula) rc, NOT);
                if(notInOrChain != null){
                    if(notInOrChain.getOperand().isOperator(f.getOperator().getMirrorOperator())){
                        rearrangeInnerFormula((BinaryFormula) rc, notInOrChain);
                    }
                }
                else {
                    BinaryFormula andInOrChain = (BinaryFormula) operatorChainSearch((BinaryFormula) rc, AND);

                    if(andInOrChain != null){
                        if(operatorChainSearch(andInOrChain, f.getOperator().getMirrorOperator()) != null){
                            setupOperatorChain(
                                    andInOrChain,
                                    f.getOperator().getMirrorOperator()
                            );
                            setupOperatorChain((BinaryFormula) rc, AND);
                            f.setRoperand(distributionRule((BinaryFormula) rc));
                        }
                    }
                }
            }
            else{

                setupOperatorChain(
                        (BinaryFormula) rc,
                        f.getOperator().getMirrorOperator()
                );
            }

        }
        /* if the left operand of f has a NOT operator and its operand has an OR one then
        *  set as the left child the formula obtained by the application of the De Morgan Rule
        * on the OR node */
        if(lc.isOperator(NOT)){
            UnaryFormula ulc = (UnaryFormula) lc;
            if(ulc.getOperand().isOperator(OR)){
                f.setLoperand(deMorganLaw(ulc));
            }

        }
        /* if the right operand of f has a NOT operator and its operand has an AND one then
         *  set as the left child the formula obtained by the application of the De Morgan Rule
         * on the AND node */
        if(rc.isOperator(NOT)){
            UnaryFormula urc = (UnaryFormula) rc;
            if(urc.getOperand().isOperator(AND)){
                f.setLoperand(deMorganLaw(urc));
            }
        }
    }

    /** Rearranges the chain of operators starting at f so that the formula with operator op is the right child of f.
     * @param f The starting node of the operator chain
     * @param op The operator of the formula that should be moved */
    private static void setupOperatorChain(BinaryFormula f, Operator op){

        /* Do the search of the operator op inside the operator chain starting at node f */
        Formula searchedOperator = operatorChainSearch(f, op);

        /* if the formula with operator op was found then */
        if(searchedOperator != null) {
            rearrangeInnerFormula(f, searchedOperator);
        }
    }

    private static void rearrangeInnerFormula(BinaryFormula f, Formula sf) {

        BinaryFormula sfPar = (BinaryFormula) sf.getParent();

        // LEFT SUBTREE
        /* if the searched operator is inside the left subtree of f then
        swap it with the right child of f */
        if (f.inLeftSubtree(sf)) {

            Formula rf = f.getRoperand();

                /* if the searched operator formula is the left child of its parent then
                swap it with the right operand of f */
            if (sfPar.isLeftChild(sf)) {
                f.setRoperand(sf);
                sfPar.setLoperand(rf);
            }
                /* if the searched operator formula is a right child and its parent is not f then
                swap it with the right operand of f */
            else if (!(sfPar.equals(f)) && sfPar.isRightChild(sf)) {
                f.setRoperand(sf);
                sfPar.setRoperand(rf);
            }
        }


        // RIGHT SUBTREE
            /* if the searched operator is inside the right subtree of f then
            swap it with the left subtree of f and then flip the two children of f */
        else if (f.inRightSubtree(sf)) {

            Formula lf = f.getLoperand();

                /* if the searched operator formula is the left child of its parent then
                swap it with the left operand of f */
            if (sfPar.isLeftChild(sf)) {
                f.setLoperand(sf);
                sfPar.setLoperand(lf);
            }

                /* if the searched operator formula is the right child of its parent then
                swap it with the left operand of f */
            else if (sfPar.isRightChild(sf)) {
                f.setLoperand(sf);
                sfPar.setRoperand(lf);
            }
            f.swapChildren();
        }

    }

    /** Search, inside a chain of operators of the same type, the operator op.
     * @param f The node on which start the search
     * @param op The operator to be search inside the operator chain
     * @return Returns a formula for which the operator corresponds to op */
    private static Formula operatorChainSearch(BinaryFormula f, Operator op) {

        /* if the right child operator of f corresponds to the operator op then
        return the left child of f */
        if(f.getRoperand().isOperator(op)) return f.getRoperand();

        /* if the left child operator of f corresponds to the operator op then
        return the left child of f */
        if(f.getLoperand().isOperator(op)) return f.getLoperand();

        /* if the operator of the right child of f is the same of f then
        do the search on the right child of f and if not null return its result */
        if(f.getRoperand().isOperator(f.getOperator())) {
            Formula result = operatorChainSearch(
                    (BinaryFormula) f.getRoperand(), op
            );
            if(result != null) return result;
        }

        /* if the operator of the left child of f is the same of f then
        do the search on the left child of f and if not null return its result */
        if(f.getLoperand().isOperator(f.getOperator())) {
            Formula result = operatorChainSearch(
                    (BinaryFormula) f.getLoperand(), op
            );
            if(result != null) return result;
        }

        /* for all the remaining cases return null */
        return null;
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
