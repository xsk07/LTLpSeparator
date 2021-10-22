package separator;

import formula.*;
import java.util.*;
import static formula.AtomConstant.*;
import static formula.BooleanRules.*;
import static formula.Operator.*;
import static separator.EliminationRules.*;
import static separator.Lemmas.*;
import static separator.OperatorChain.getBorderNodes;


public class FormulaSeparator {

    private Formula root;

    public void setRoot(Formula f) { root = f; }

    public Formula getRoot() { return root; }

    public void updateRoot(Formula f) {
        if(f.getParent() == null && f != root) setRoot(f);
    }

    private static boolean needsNormalization(Formula f) {
        Queue<Formula> q = new LinkedList<>();
        q.add(f);
        while(!q.isEmpty()) {
            Formula nf = q.remove();
            if(nf.isOperator()) {
                OperatorFormula onf = (OperatorFormula) nf;
                switch(onf.getOperator()) {
                    case NOT: {
                        UnaryFormula unf = (UnaryFormula) onf;
                        if(needsInvolution(unf)) return true;
                        if(needsDeMorganLaw(unf)) return true;
                        break;
                    }
                    case OR: {
                        BinaryFormula bnf = (BinaryFormula) onf;
                        q.add(bnf.getLoperand());
                        q.add(bnf.getRoperand());
                        break;
                    }
                    case AND: {
                        BinaryFormula bnf = (BinaryFormula) onf;
                        if(needsDistributiveLaw(bnf)) return true;
                        q.add(bnf.getLoperand());
                        q.add(bnf.getRoperand());
                        break;
                    }
                }
            }
        }
        return false;
    }



    private static Formula applyNormalizations(Formula f) {
        Queue<Formula> q = new LinkedList<>();
        q.add(f);
        while(!q.isEmpty()) {
            Formula nf = q.remove();
            if(nf.isOperator()) {
                OperatorFormula onf = (OperatorFormula) nf;
                switch(onf.getOperator()) {
                    case NOT: {
                        UnaryFormula unf = (UnaryFormula) onf;
                        if(needsInvolution(unf)) {
                            nf = unf.replaceFormula(involution(unf));
                            f = updateF(f, nf);
                            q.add(nf);
                        }
                        if(needsDeMorganLaw(unf)) {
                            nf = unf.replaceFormula(deMorganLaw(unf));
                            f = updateF(f, nf);
                            q.add(nf);
                        }
                        break;
                    }
                    case OR: {
                        BinaryFormula bnf = (BinaryFormula) onf;
                        q.add(bnf.getLoperand());
                        q.add(bnf.getRoperand());
                        break;
                    }
                    case AND: {
                        BinaryFormula bnf = (BinaryFormula) onf;
                        if(needsDistributiveLaw(bnf)) {
                            nf = bnf.replaceFormula(distributiveLaw(bnf));
                            f = updateF(f, nf);
                            q.add(nf);
                        }
                        else {
                            q.add(bnf.getLoperand());
                            q.add(bnf.getRoperand());
                        }
                        break;
                    }
                }
            }
        }
        return f;
    }

    public static Formula normalize(Formula f) {
        while (needsNormalization(f)) {
        //int i = 1;
        //while(i != 0) {
            //i--;
            f = applyNormalizations(f);
        }
        return f;
    }


    /** @return Returns the matrix representing the formula f got in input */
    public static PureFormulaeMatrix getPureFormulaeMatrix(Formula f) {
        PureFormulaeMatrix matrix = new PureFormulaeMatrix();

        ArrayList<Formula> conjunctions = getConjunctions(f);
        for (Formula c : conjunctions) {
            FormulaeList pastList = new FormulaeList();
            FormulaeList presentList = new FormulaeList();
            FormulaeList futureList = new FormulaeList();

            ArrayList<Formula> pureFormulas = getPureFormulae(c);
            for (Formula p : pureFormulas) {
                switch (p.getTime()){
                    case PAST -> pastList.add(p);
                    case PRESENT -> presentList.add(p);
                    case FUTURE -> futureList.add(p);
                }
            }

            matrix.addTriple(
                    pastList.toConjunctionFormula(),
                    presentList.toConjunctionFormula(),
                    futureList.toConjunctionFormula()
            );
        }

        return matrix;
    }

    /* pre:
     * - f is in DNF form;
     * - or is a pure formula;
     * - or a conjunction of pure formulas; */
    private static ArrayList<Formula> getConjunctions(Formula f) {
        ArrayList<Formula> cnjs = new ArrayList<>();
        if(f.isOperator(OR)) cnjs = getBorderNodes((BinaryFormula) f);
        else cnjs.add(f);
        return cnjs;
    }

    /*pre: f is atomic or binary */
    private static ArrayList<Formula> getPureFormulae(Formula f) {
        ArrayList<Formula> pureFms = new ArrayList<>();
        if(f.isOperator(AND)) pureFms = getBorderNodes((BinaryFormula) f);
        else pureFms.add(f);
        return pureFms;
    }


    /** Separates the formula f got in input, which means that extracts the nested occurrences of U inside S and viceversa.
     * @return A Formula which is a combination of pure past, pure present and pure future formulas
     * @param f the formula which needs to be separated */
    public static Formula separate(Formula f) {

        Formula nf = f;

        /* while the formula is not separated */
        while(!f.isSeparated()) {

        //int i = 0;
        //while(i != 0) {

            //i--;
            /* search a node x that could be eliminated */
            BinaryFormula x = searchX(f);

            /* the node x is never null because if the formula is not separated then
             * must exist a subformula which could be eliminated */
            assert x != null;

            /* candidate nodes to be the y of a junction in the left subtree of x */
            ArrayList<OperatorFormula> lys = x.getLoperand().searchOperators(
                    x.getOperator().getMirrorOperator()
            );

            /* candidate nodes to be the y of a junction in the right subtree of x */
            ArrayList<OperatorFormula> rys = x.getRoperand().searchOperators(
                    x.getOperator().getMirrorOperator()
            );

            /* if it is possible apply on x some elimination
             * an update if needed the resulting formula */
            if(isEliminable(x)) {
                f = updateF(f, x.replaceFormula(applyElimination(x)));
            }

            /* if is not applicable any elimination rule then
             * the formula arrange the formula into an eliminable form */
            else {

                switch (lys.size()) {
                    case 0: { // no ys in left subtree
                        switch (rys.size()) {
                            case 0: break; // no ys in right subtree
                            default: {
                                f = updateF(f, subtreeJunctionOperations(x, rys));
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        switch (rys.size()) {
                            case 0: { // no ys in right subtree
                                f = updateF(f, subtreeJunctionOperations(x, lys));
                                break;
                            }
                            default: {
                                nf = subtreeJunctionOperations(x, lys);
                                if(nf == f) nf = subtreeJunctionOperations(x, rys);
                                f = updateF(f, nf);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return f;
    }

    private static Formula updateF(Formula f, Formula nf) {
        if(nf.getParent() == null && nf != f) return nf;
        return f;
    }

    public static Formula subtreeJunctionOperations(BinaryFormula x, ArrayList<OperatorFormula> ys) {
        /* create a list with all the junctions from x to the ys contained in js */
        ArrayList<JunctionPath> js = new ArrayList<>();
        ys.forEach(y -> js.add(new JunctionPath(x, (BinaryFormula) y)));
        /* if all the ys inside js are arranged then get the first junction
         * if it is needed to apply the lemma A2 then apply it and if it is
         * needed update the root of the formula tree return the new root */
        if(areArranged(js)) {

            JunctionPath j;
            j = js.iterator().next();
            BinaryFormula y = j.getY();


            if (needsLemmaA2AND(x) && y.isInRightSubtreeOf(x)) {
                return x.replaceFormula(lemmaA2AND(x));
            }
            else if (needsLemmaA2OR(x) && y.isInLeftSubtreeOf(x)) {
                return x.replaceFormula(lemmaA2OR(x));
            }

            else if(j.isOperatorChain()) {

                j.setupOperatorChain();
                OperatorFormula y2 = (OperatorFormula) j.getX().searchSameOperatorFormula(j.getY());
                if(y2 != null) {
                    boolean twoEqualsY = y2.equals(j.getY());
                    boolean sameParentOperators =  y2.getParent().getOperator().equals(j.getY().getParent().getOperator());
                    if(twoEqualsY || sameParentOperators) {
                        underOneConjunction(j.getY(), (BinaryFormula) y2);
                    }
                }
            }

        }
        /* if there is some junction that needs to be arranged
         * get the first one and arrange it */
        else {
            JunctionPath j = getJunctionToArrange(js);
            assert j != null;
            j.arrange();
        }

        return x;
    }


    /** Returns the first find junction to be arranged */
    public static JunctionPath getJunctionToArrange(ArrayList<JunctionPath> js) {
        for (JunctionPath j : js) if(j.needsArrangement()) return j;
        return null;
    }

    /** Returns true if, and only if, all the junctions inside the passed
      * array list are arranged */
    public static boolean areArranged(ArrayList<JunctionPath> js) {
        boolean b = true;
        for (JunctionPath j : js) {
            if(!j.isArranged()) b = false;
        }
        return b;
    }



    // pre: j1, j2 are operator chains both setupped
    private static Formula underOneConjunction(BinaryFormula f1, BinaryFormula f2) {


        OperatorFormula y1 = f1; // set y1 by default to f1
        OperatorFormula y2 = f2; // set y2 by default to f2
        BinaryFormula and1 = null;
        BinaryFormula and2 = null;
        OperatorFormula p1 = y1.getParent(); // save the parent of y1
        OperatorFormula p2 = y2.getParent(); // save the parent of y2

        /* if the parent of y1 is a node with operator NOT then
         * set it like the new y1 and update the and1 to the parent
         * of the new y1 which corresponds to y1 */
        if(p1.isOperator(NOT)) {
            y1 = p1;
            and1 = (BinaryFormula) y1.getParent();
        }
        else and1 = (BinaryFormula) p1.getParent();

        if(p2.isOperator(NOT)) {
            y2 = p2;
            and2 = (BinaryFormula) y2.getParent();
        }
        else and2 = (BinaryFormula) p2;

        if(and1.isAncestorOf(y2)) return and2.replaceFormula(
                unify(swapFirstWithBrotherOfSecond(y1, y2))
        );

        else return and1.replaceFormula(
                    unify(swapFirstWithBrotherOfSecond(y2, y1))
            );
    }


    private static BinaryFormula swapFirstWithBrotherOfSecond(Formula f1, Formula f2) {

        BinaryFormula and1 = (BinaryFormula) f1.getParent();
        BinaryFormula and2 = (BinaryFormula) f2.getParent();

        // if y1 is the left child of and1
        if(f1.isLeftChildOf(and1)) {

            /* if y2 is the left child of and2 then
             * first swap y1 with the right child of and2 */
            if(f2.isLeftChildOf(and2)) {
                Formula r2 = and2.getRoperand();
                and2.setRoperand(f1);
                and1.setLoperand(r2);
            }
            if(f2.isRightChildOf(and2)) {
                Formula l2 = and2.getLoperand();
                and2.setLoperand(f1);
                and1.setLoperand(l2);
            }

        }

        // if y1 is the right child of and1
        else if(f1.isRightChildOf(and1)) {

            /* if y2 is the left child of and2 then
             * first swap y1 with the right child of and2 */
            if(f2.isLeftChildOf(and2)) {
                Formula r2 = and2.getRoperand();
                and2.setRoperand(f1);
                and1.setRoperand(r2);
            }
            if(f2.isRightChildOf(and2)) {
                Formula l2 = and2.getLoperand();
                and2.setLoperand(f1);
                and1.setRoperand(l2);
            }
        }

        return and2;
    }


    public static Formula unify(BinaryFormula f) {

        OperatorFormula lc = (OperatorFormula) f.getLoperand();
        OperatorFormula rc = (OperatorFormula) f.getRoperand();

        /* if the two children represent the same formula return one of them */
        if(lc.equals(rc)) return f.replaceFormula(lc);
        else { // if the two formulas are different then

            /* if the two children are negations then
             * pull out the not operator and apply the unification
             * on its child, for instance:
             * !(aSb)&!(bSc) =>* !((aSb)|(bSc)) */
            if(lc.isOperator(NOT) && rc.isOperator(NOT)) {
                UnaryFormula ulc = (UnaryFormula) lc;
                UnaryFormula urc = (UnaryFormula) rc;
                return f.replaceFormula(
                        BooleanRules.negate(unify(new BinaryFormula(
                                        f.getOperator().getMirrorOperator(),
                                        ulc.getOperand(),
                                        urc.getOperand())
                                )
                        )
                );
            }

            /* if the two children are not equal that means one of them is the negation
             * of the other or they are completely different.
             * In the first case return a new atomic formula.
             * If the operator of f is an AND return the atom FALSE;
             * If the operator of f is an OR return the atom TRUE; */
            if(lc.isNegationOf(rc) || rc.isNegationOf(lc)) {
                AtomConstant tVal = FALSE;
                if(f.isOperator(OR)) tVal = TRUE;
                return f.replaceFormula(new AtomicFormula(tVal));
            }
            /* In the second case if both the children are binary formulas with the same operator
             * and their left child are equal then apply on f the reversed lemma A.2
             * else apply the lemmaA1. */
            if(lc.isBinary() && rc.isBinary()) {
                BinaryFormula blc = (BinaryFormula) lc;
                BinaryFormula brc = (BinaryFormula) rc;
                boolean sameOperator = blc.getOperator().equals(brc.getOperator());
                boolean sameLeftOperand = blc.getLoperand().equals(brc.getLoperand());
                boolean sameRightOperand = blc.getRoperand().equals(brc.getRoperand());
                if(sameOperator){
                    if(f.isOperator(OR) && sameRightOperand) {
                        return f.replaceFormula(reversedLemmaA2(f));
                    }
                    if(f.isOperator(AND) && sameLeftOperand) {
                        return f.replaceFormula(reversedLemmaA2(f));
                    }
                    else {
                        return f.replaceFormula(lemmaA1(f));
                    }
                }
            }
        }
        // in the all remaining cases return f itself
        return f;
    }


    /** Searches a candidate node to be an x in the junction.
     *  @return returns a BinaryFormula that need to be separated
     *  and whose children are already separated
     *  @param phi the Formula from which start the search */
    public static BinaryFormula searchX(Formula phi) {
        Queue<Formula> q = new LinkedList<>();
        q.add(phi);
        while(!q.isEmpty()) {
            Formula f = q.remove();
            if(f.needSeparation()) {
                if(f.isOperator()) {
                    OperatorFormula of = (OperatorFormula) f;
                    /* if f is a UnaryFormula then
                    add to the search queue its child */
                    if(of.isUnary()) {
                        UnaryFormula uf = (UnaryFormula) of;
                        q.add(uf.getOperand());
                    }
                    /* if f is a BinaryFormula and needs to be separated
                     * then if it has UNTIL or SINCE as operator and
                     * its children are already separated then return f */
                    if(of.isBinary()) {
                        BinaryFormula bf = (BinaryFormula) of;
                        if(bf.isOperator(UNTIL) || bf.isOperator(SINCE)) {
                            Formula lc = bf.getLoperand();
                            Formula rc = bf.getRoperand();
                            if(lc.isSeparated() && rc.isSeparated()) return bf;
                        }
                        /* if the operator of f is a BOOLEAN operator
                         * or one of its children is not separated then
                         * add the two children to the search queue */
                        q.add(bf.getRoperand());
                        q.add(bf.getLoperand());
                    }
                }
            }
        }
        return null;
    }


    public static OperatorFormula getNextOperator(OperatorFormula phi, Operator op) {
        Queue<Formula> q = new LinkedList<>();
        if(phi.isUnary()) {
            UnaryFormula uPhi = (UnaryFormula) phi;
            q.add(uPhi.getOperand());
        }
        if(phi.isBinary()){
            BinaryFormula bPhi = (BinaryFormula) phi;
            q.add(bPhi.getLoperand());
            q.add(bPhi.getRoperand());
        }
        // while the queue is not empty repeat
        while(!q.isEmpty()) {
            Formula f = q.remove();
            if(f.isOperator()){
                OperatorFormula of = (OperatorFormula) f;
                /* if the operator is found add the corresponding formula to the result
                 * else iterate the search on the children nodes */
                if(of.isOperator(op)) return of;
                else {
                    /* if the operator is different from the mirror operator of op then
                     * perform the search on its children nodes, if is find a mirror operator
                     * the search is interrupted on this branch */
                    if(of.getOperator() != op.getMirrorOperator()){
                        /* if f is a unary formula then perform the search on its operand,
                        so it is added to the queue */
                        if(of.isUnary()){
                            UnaryFormula uf = (UnaryFormula) of;
                            q.add(uf.getOperand());
                        }
                        if(of.isBinary()){
                            /* if f is a binary formula then perform the search on its operands,
                             * so they are added to the queue */
                            BinaryFormula bf = (BinaryFormula) of;
                            q.add(bf.getLoperand());
                            q.add(bf.getRoperand());
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean existTwoSameFormulas(OperatorFormula x) {
        ArrayList<OperatorFormula> al = x.searchOperators(x.getOperator().getMirrorOperator());
        boolean b = false;
        for (OperatorFormula y : al) {
            b = (x.searchSameOperatorFormula(y) != null);
        }
        return b;
    }


    public static boolean isEliminable(BinaryFormula x) {

        /* candidate nodes to be the y of a junction in the left subtree of x */
        ArrayList<OperatorFormula> lys = x.getLoperand().searchOperators(
                x.getOperator().getMirrorOperator()
        );

        /* candidate nodes to be the y of a junction in the right subtree of x */
        ArrayList<OperatorFormula> rys = x.getRoperand().searchOperators(
                x.getOperator().getMirrorOperator()
        );

        ArrayList<JunctionPath> ljs = new ArrayList<>();
        lys.forEach(y -> ljs.add(new JunctionPath(x, (BinaryFormula) y)));

        ArrayList<JunctionPath> rjs = new ArrayList<>();
        rys.forEach(y -> rjs.add(new JunctionPath(x, (BinaryFormula) y)));

        switch (ljs.size()) {
            /* if there are no junctions in the left subtree then */
            case 0: {
                /* if in the right subtree there is only one junction then
                 * if it is an operator chain and is in eliminable form then
                 * return true, false otherwise */
                if (rjs.size() == 1) {
                    JunctionPath rj = rjs.get(0);
                    return rj.isOperatorChain() && rj.eliminableForm();
                }
                /* if in right subtree there is more than one junction then
                 * remove from the list the first find junction and use it as reference,
                 * then remove from the junction list all the junctions whose y parent operator
                 * is different from the y parent operator of the junction chose as reference
                 * if the junction list is empty and the chosen junction is an operator chain
                 * and is in an eliminable form then return true, otherwise return false */
                if(rjs.size() > 1) {
                    JunctionPath rj = rjs.remove(0);
                    rys.removeIf(y -> !y.getParent().getOperator().equals(rj.getY().getParent().getOperator()));
                    return (rj.isOperatorChain() && rj.eliminableForm() && rys.isEmpty());
                }
                return false;
            }
            case 1: {
                if (rjs.size() == 0) {
                    JunctionPath lj = ljs.get(0);
                    return lj.isOperatorChain() && lj.eliminableForm();
                }
                if (rjs.size() == 1) {
                    JunctionPath lj = ljs.get(0);
                    JunctionPath rj = rjs.get(0);
                    return (lj.isOperatorChain() && lj.eliminableForm() && rj.isOperatorChain() && rj.eliminableForm());
                }
                return false;
            }
            default: {
                JunctionPath lj = ljs.remove(0);
                ljs.removeIf(j -> !j.getY().getParent().getOperator().equals(lj.getY().getParent().getOperator()));
                return (lj.isOperatorChain() && lj.eliminableForm() && lys.isEmpty());
            }
        }
    }


    public static Formula applyElimination(BinaryFormula f) {

        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula should be S or U but is %s",
                            f.getOperator()
                    )
            );
        }

        int nc = nestingCase(f);
        ArrayList<Formula> sfms;
        switch (nc) {
            case 1 -> {
                sfms = subformulas1(f);
                System.out.println("Elimination1");
                return elimination1(sfms, f.getOperator());
            }
            case 2 -> {
                sfms = subformulas2(f);
                System.out.println("Elimination2");
                return elimination2(sfms, f.getOperator());
            }
            case 3 -> {
                sfms = subformulas3(f);
                System.out.println("Elimination3");
                return elimination3(sfms, f.getOperator());
            }
            case 4 -> {
                sfms = subformulas4(f);
                System.out.println("Elimination4");
                return elimination4(sfms, f.getOperator());
            }
            case 5 -> {
                sfms = subformulas57(f);
                System.out.println("Elimination5");
                return elimination5(sfms, f.getOperator());
            }
            case 6 -> {
                sfms = subformulas6(f);
                System.out.println("Elimination6");
                return elimination6(sfms, f.getOperator());
            }
            case 7 -> {
                sfms = subformulas57(f);
                System.out.println("Elimination7");
                return elimination7(sfms, f.getOperator());
            }
            case 8 -> {
                sfms = subformulas8(f);
                System.out.println("Elimination8");
                return elimination8(sfms, f.getOperator());
            }
            default -> { return f; }
        }
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

            if(ofX.getOperator() == AND) {

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
    public static ArrayList<Formula> subformulas6(BinaryFormula f) {
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
