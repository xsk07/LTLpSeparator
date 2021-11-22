package separator;

import formula.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import static formula.BinaryFormula.newCombination;
import static formula.BinaryFormula.newConjunction;
import static formula.Operator.*;
import static separator.Lemmata.*;
import static separator.eliminator.FormulaEliminator.applyElimination;
import static formula.Junction.junctionsList;
import static separator.FormulaSimplifier.simplify;

public class FormulaSeparator {

    private Formula root;

    public void setRoot(Formula f) { root = f; }

    public Formula getRoot() { return root; }

    public void updateRoot(Formula f) {
        if(f.getParent() == null && f != root) setRoot(f);
    }

    /** Returns true if, and only if, all the junctions inside the passed
     * array list are arranged */
    private boolean areArranged(ArrayList<Junction> js) {
        AtomicBoolean b = new AtomicBoolean(true);
        js.forEach(j -> { if(!j.isArranged()) b.set(false);});
        return b.get();
    }

    private boolean areOperatorChains(ArrayList<Junction> js) {
        AtomicBoolean b = new AtomicBoolean(true);
        js.forEach(j -> { if(!j.isOperatorCombination()) b.set(false); });
        return b.get();
    }

    public static int junctionCase(Junction j) {
        if(j == null) return 0;
        if(j.getY().getParent().isOperator(NOT)) return 2;
        if(j.getY().getParent().isOperator(j.getOperator())) return 1;
        return 0;
    }

    /** returns the maximum k value of the top junctions of the paths in f */
    public static int maxKofDegreeM(Formula f, int m) {
        if(m == 0) return 0;
        List<Path> paths = f.getPaths().stream().filter(p -> p.getM() == m).toList();
        List<Junction> topJunctions = new LinkedList<>();
        paths.forEach(p -> topJunctions.add(p.getTopJunction()));
        IntStream junctionsMaxK = topJunctions.stream().mapToInt(Junction::getK);
        return junctionsMaxK.max().getAsInt();
    }


    private BinaryFormula applyLemmata(BinaryFormula x, ArrayList<Junction> ljs, ArrayList<Junction> rjs) {
        // Lemmata Tasks

        /* if the left subtree of x contains more than one junction then */
        if(ljs.size() > 1) {

            if(x.getLoperand() instanceof BinaryFormula lx) {
                List<Formula> operands = lx.getCombinationOperands();

                /* list of operand formulae whose operator is mirror to the one of x */
                List<UnaryFormula> negCandidates = new LinkedList<>();
                BinaryFormula finalX = x;
                operands.forEach(z -> {
                    if(z instanceof UnaryFormula uz && uz.isOperator(NOT)){
                        if(uz.getOperand().isOperator(finalX.getOperator().getMirrorOperator())) negCandidates.add(uz);
                    }});

                /* list of operand formulae whose operator is mirror to the one of x */
                List<BinaryFormula> candidates = new LinkedList<>();
                BinaryFormula finalX1 = x;
                operands.forEach(z -> {
                    if(z instanceof BinaryFormula bz){
                        if(bz.isOperator(finalX1.getOperator().getMirrorOperator())) candidates.add(bz);
                    }});

                /* for each couple of negate candidates for the application of one of the Lemmata */
                for (UnaryFormula f1 : negCandidates) {
                    for (UnaryFormula f2 : negCandidates) {
                        /* determine the children respectively c1 e c2*/
                        BinaryFormula c1 = (BinaryFormula) f1.getOperand();
                        BinaryFormula c2 = (BinaryFormula) f2.getOperand();
                        /* if the two formulae are different and needs the application of the
                           reversedLemmaA2 then */
                        if (f1 != f2 && needReversedLemmaA2(AND, c1, c2)) {
                            /* perform a deep copy of the operands of the combination
                             * without the two candidates */
                            ArrayList<Formula> operandsDeepCopy = new ArrayList<>();
                            ArrayList<Formula> lx_operands = lx.getCombinationOperands();
                            /* remove from the list of operands the two candidates */
                            lx_operands.remove(f1);
                            lx_operands.remove(f2);
                            /* perform the deep copy */
                            lx_operands.forEach(opr -> operandsDeepCopy.add(opr.deepCopy()));
                            /* apply the Lemma A2 */
                            Formula formulaLA2 = reversedLemmaA2(AND, c1.deepCopy(), c2.deepCopy()).negate();
                            operandsDeepCopy.add(formulaLA2);
                            Formula newOperand = newConjunction(operandsDeepCopy);
                            BinaryFormula newX = new BinaryFormula(x.getOperator(), newOperand, x.getRoperand().deepCopy());
                            /* ensure that the formula obtained by the application of Lemma A2 produces
                            *  a new formula of lower degree m or equal degree of m but less or equal k degree */
                            int prevM = x.degree();
                            int mNewX  = newX.degree();
                            if(mNewX <= prevM) {
                                boolean replaceable = false;
                                if(mNewX == prevM) {
                                    if(maxKofDegreeM(newX, mNewX) <= maxKofDegreeM(x, prevM)) replaceable = true;
                                }
                                else replaceable = true;
                                if(replaceable) {
                                    x = (BinaryFormula) x.replaceFormula(newX);
                                    updateRoot(x);
                                    return x;
                                }
                            }
                        }
                    }
                }


                /* for each couple of candidates for the application of one of the Lemmata */
                for (BinaryFormula f1 : candidates) {
                    for (BinaryFormula  f2 : candidates) {
                        /* if the two formulae are different and needs the application of the
                           reversedLemmaA2 then */
                        if (f1 != f2 && needReversedLemmaA2(AND, f1, f2)) {
                            /* perform a deep copy of the operands of the combination
                             * without the two candidates */
                            ArrayList<Formula> operandsDeepCopy = new ArrayList<>();
                            ArrayList<Formula> lx_operands = lx.getCombinationOperands();
                            /* remove from the list of operands the two candidates */
                            lx_operands.remove(f1);
                            lx_operands.remove(f2);
                            /* perform the deep copy */
                            lx_operands.forEach(opr -> operandsDeepCopy.add(opr.deepCopy()));
                            /* apply the Lemma A2 */
                            Formula formulaLA2 = reversedLemmaA2(AND, f1.deepCopy(), f2.deepCopy());
                            operandsDeepCopy.add(formulaLA2);
                            Formula newOperand = newConjunction(operandsDeepCopy);
                            BinaryFormula newX = new BinaryFormula(x.getOperator(), newOperand, x.getRoperand().deepCopy());
                            /* ensure that the formula obtained by the application of Lemma A2 produces
                             *  a new formula of lower degree m or equal degree of m but less or equal k degree */
                            int prevM = x.degree();
                            int mNewX  = newX.degree();
                            if(mNewX <= prevM) {
                                boolean replaceable = false;
                                if(mNewX == prevM) {
                                    if(maxKofDegreeM(newX, mNewX) <= maxKofDegreeM(x, prevM)) replaceable = true;
                                }
                                else replaceable = true;
                                if(replaceable) {
                                    x = (BinaryFormula) x.replaceFormula(newX);
                                    updateRoot(x);
                                    return x;
                                }
                            }
                        }
                    }
                }

                // LEMMA A1

                /* for each couple of candidates for the application of one of the Lemmata */
                for (BinaryFormula f1 : candidates) {
                    for (BinaryFormula  f2 : candidates) {
                        /* if the two formulae are different and needs the application of the
                           reversedLemmaA2 then */
                        if (f1 != f2) {
                            /* perform a deep copy of the operands of the combination
                             * without the two candidates */
                            ArrayList<Formula> operandsDeepCopy = new ArrayList<>();
                            ArrayList<Formula> lx_operands = lx.getCombinationOperands();
                            /* remove from the list of operands the two candidates */
                            lx_operands.remove(f1);
                            lx_operands.remove(f2);
                            /* perform the deep copy */
                            lx_operands.forEach(opr -> operandsDeepCopy.add(opr.deepCopy()));
                            /* apply the Lemma A2 */
                            Formula formulaLA1 = Lemmata.lemmaA1(f1.deepCopy(), f2.deepCopy());
                            operandsDeepCopy.add(formulaLA1);
                            Formula newOperand = newConjunction(operandsDeepCopy);
                            BinaryFormula newX = new BinaryFormula(x.getOperator(), newOperand, x.getRoperand().deepCopy());
                            /* ensure that the formula obtained by the application of Lemma A2 produces
                             *  a new formula of lower degree m or equal degree of m but less or equal k degree */
                            int prevM = x.degree();
                            int mNewX  = newX.degree();
                            if(mNewX <= prevM) {
                                x = (BinaryFormula) x.replaceFormula(newX);
                                updateRoot(x);
                                return x;
                            }
                        }
                    }
                }
            }
        }

        if(rjs.size() > 1) {

            if(x.getRoperand() instanceof BinaryFormula rx) {
                List<Formula> operands = rx.getCombinationOperands();

                /* list of operand formulae whose operator is mirror to the one of x */
                List<UnaryFormula> negCandidates = new LinkedList<>();
                BinaryFormula finalX = x;
                operands.forEach(z -> {
                    if (z instanceof UnaryFormula uz && uz.isOperator(NOT)) {
                        if (uz.getOperand().isOperator(finalX.getOperator().getMirrorOperator())) negCandidates.add(uz);
                    }
                });

                /* list of operand formulae whose operator is mirror to the one of x */
                List<BinaryFormula> candidates = new LinkedList<>();
                BinaryFormula finalX1 = x;
                operands.forEach(z -> {
                    if (z instanceof BinaryFormula bz) {
                        if (bz.isOperator(finalX1.getOperator().getMirrorOperator())) candidates.add(bz);
                    }
                });

                /* for each couple of negate candidates for the application of one of the Lemmata */
                for (UnaryFormula f1 : negCandidates) {
                    for (UnaryFormula f2 : negCandidates) {
                        /* determine the children respectively c1 e c2*/
                        BinaryFormula c1 = (BinaryFormula) f1.getOperand();
                        BinaryFormula c2 = (BinaryFormula) f2.getOperand();
                        /* if the two formulae are different and needs the application of the
                           reversedLemmaA2 then */
                        if (f1 != f2 && needReversedLemmaA2(OR, c1, c2)) {
                            /* perform a deep copy of the operands of the combination
                             * without the two candidates */
                            ArrayList<Formula> operandsDeepCopy = new ArrayList<>();
                            ArrayList<Formula> rx_operands = rx.getCombinationOperands();
                            /* remove from the list of operands the two candidates */
                            rx_operands.remove(f1);
                            rx_operands.remove(f2);
                            /* perform the deep copy */
                            rx_operands.forEach(opr -> operandsDeepCopy.add(opr.deepCopy()));
                            /* apply the Lemma A2 */
                            Formula formulaLA2 = reversedLemmaA2(OR, c1.deepCopy(), c2.deepCopy()).negate();
                            operandsDeepCopy.add(formulaLA2);
                            Formula newOperand = newConjunction(operandsDeepCopy);
                            BinaryFormula newX = new BinaryFormula(x.getOperator(), x.getLoperand().deepCopy(), newOperand);
                            /* ensure that the formula obtained by the application of Lemma A2 produces
                             *  a new formula of lower degree m or equal degree of m but less or equal k degree */
                            int prevM = x.degree();
                            int mNewX = newX.degree();
                            if (mNewX <= prevM) {
                                boolean replaceable = false;
                                if (mNewX == prevM) {
                                    if (maxKofDegreeM(newX, mNewX) <= maxKofDegreeM(x, prevM)) replaceable = true;
                                } else replaceable = true;
                                if (replaceable) {
                                    x = (BinaryFormula) x.replaceFormula(newX);
                                    updateRoot(x);
                                    return x;
                                }
                            }
                        }
                    }
                }


                /* for each couple of candidates for the application of one of the Lemmata */
                for (BinaryFormula f1 : candidates) {
                    for (BinaryFormula f2 : candidates) {
                        /* if the two formulae are different and needs the application of the
                           reversedLemmaA2 then */
                        if (f1 != f2 && needReversedLemmaA2(OR, f1, f2)) {
                            /* perform a deep copy of the operands of the combination
                             * without the two candidates */
                            ArrayList<Formula> operandsDeepCopy = new ArrayList<>();
                            ArrayList<Formula> rx_operands = rx.getCombinationOperands();
                            /* remove from the list of operands the two candidates */
                            rx_operands.remove(f1);
                            rx_operands.remove(f2);
                            /* perform the deep copy */
                            rx_operands.forEach(opr -> operandsDeepCopy.add(opr.deepCopy()));
                            /* apply the Lemma A2 */
                            Formula formulaLA2 = reversedLemmaA2(OR, f1.deepCopy(), f2.deepCopy());



                            operandsDeepCopy.add(formulaLA2);
                            Formula newOperand = newConjunction(operandsDeepCopy);
                            BinaryFormula newX = new BinaryFormula(x.getOperator(), x.getLoperand().deepCopy(), newOperand);
                            /* ensure that the formula obtained by the application of Lemma A2 produces
                             *  a new formula of lower degree m or equal degree of m but less or equal k degree */
                            int prevM = x.degree();
                            int mNewX = newX.degree();
                            if (mNewX <= prevM) {
                                boolean replaceable = false;
                                if (mNewX == prevM) {
                                    if (maxKofDegreeM(newX, mNewX) <= maxKofDegreeM(x, prevM)) replaceable = true;
                                } else replaceable = true;
                                if (replaceable) {
                                    x = (BinaryFormula) x.replaceFormula(newX);
                                    updateRoot(x);
                                    return x;
                                }
                            }
                        }
                    }
                }
            }
        }

        return x;
    }

    /** Separates the formula f got in input, which means that extracts the nested occurrences of U inside S and viceversa.
     * @return A Formula which is a combination of pure past, pure present and pure future formulas
     * @param f the formula which needs to be separated */
    public Formula separate(Formula f) throws ExecutionException, InterruptedException {
        System.out.println("Formula separation, applied rules: ");

        root = f; // initialize the root with the formula f
        Stack<BinaryFormula> xs = initializeXStack(f); // initialize the stack of xs
        // while there is some x to separate
        //int i = 0;
        //i != 0 &&
        while (!xs.isEmpty()) {
            //i--;
            BinaryFormula x = xs.pop(); // pop from the top of stack an x
            if(x.getLoperand() instanceof BinaryFormula lx) x.setLoperand(simplify(lx));
            if(x.getRoperand() instanceof BinaryFormula rx) x.setRoperand(simplify(rx));

            /* the ys nodes of the junctions in the left subtree starting from x */
            ArrayList<OperatorFormula> lys = x.getLoperand().searchOperators(x.getOperator().getMirrorOperator());
            /* the ys nodes of the junctions in the right subtree starting from x */
            ArrayList<OperatorFormula> rys = x.getRoperand().searchOperators(x.getOperator().getMirrorOperator());
            /* create a list containing the junctions in the left subtree of x */
            ArrayList<Junction> ljs = junctionsList(x, lys);
            /* create a list containing the junctions in the right subtree of x */
            ArrayList<Junction> rjs = junctionsList(x, rys);
            /* if the formula x has an eliminable form then */
            if(isEliminable(x, ljs, rjs)) {

                BinaryFormula newX = applyLemmata(x, ljs, rjs);

                if(newX != x) xs.add(newX);

                else {

                    if(ljs.size() == 1) {
                        Junction lj = ljs.iterator().next();
                        if(lj.isImmediateChild()) {lj.rewriteImmediateChild();}
                    }
                    if(rjs.size() == 1) {
                        Junction rj = rjs.iterator().next();
                        if(rj.isImmediateChild()) {rj.rewriteImmediateChild();}
                    }

                    ArrayList<Junction[]> eliminationChoices = new ArrayList<>();
                    if(ljs.size() > 0 && rjs.size() > 0) ljs.forEach(lj -> rjs.forEach(rj -> eliminationChoices.add(new Junction[] {lj, rj})));
                    if(ljs.size() > 0) ljs.forEach(lj -> eliminationChoices.add(new Junction[] {lj, null}));
                    if(rjs.size() > 0) rjs.forEach(rj -> eliminationChoices.add(new Junction[] {null, rj}));

                    ArrayList<Formula> eliminationProductions = new ArrayList<>();
                    eliminationChoices.forEach(
                            js -> eliminationProductions.add(applyElimination(js[0], js[1]))
                    );

                    /* 1. determine the productions with the min degree */
                    OptionalInt minDegree = eliminationProductions.stream().mapToInt(Formula::degree).min();
                    List<Formula> minDegreeProductions = eliminationProductions.stream().filter(p -> p.degree() == minDegree.getAsInt()).toList();
                    /* 2. map the productions of minimum degree to their maximum k value and then
                     *    choose one of those with the minimum k value */
                    OptionalInt minK  = minDegreeProductions.stream().mapToInt(p -> maxKofDegreeM(p, minDegree.getAsInt())).min();
                    List<Formula> candidates = eliminationProductions.stream().filter(p-> maxKofDegreeM(p, minDegree.getAsInt()) == minK.getAsInt()).toList();

                    //System.out.println(minDegreeProductions.size());
                    //System.out.println(candidates.size());

                    Optional<Formula> chosen = Optional.ofNullable(candidates.iterator().next());
                    Formula found = chosen.isPresent() ? chosen.get() : x;


                    //found = eliminationProductions.get(1);
                    //System.out.println(found);

                    if(found != x) {
                        Formula nx = x.replaceFormula(found);
                        updateRoot(nx);
                        // two stacks to preserve the order
                        Stack<BinaryFormula> nxs = initializeXStack(nx);
                        Stack<BinaryFormula> aux_stk = new Stack<>();
                        aux_stk.addAll(nxs);
                        xs.addAll(aux_stk);
                    }
                }

            }
            // if the formula x is not eliminable then apply the transformation to make it so
            else {
                if(needsLemmaA2OR(x) && ljs.size() > 0) {
                    // the node that will replace x
                    Formula new_x = lemmaA2OR(x);
                    ArrayList<OperatorFormula> new_xs = new_x.searchOperators(x.getOperator());
                    new_xs.forEach(nx -> {if(nx.needSeparation()) xs.push((BinaryFormula) nx); });
                    updateRoot(x.replaceFormula(new_x));
                }
                else if(needsLemmaA2AND(x) && rjs.size() > 0) {
                    // the node that will replace x
                    Formula new_x = lemmaA2AND(x);
                    ArrayList<OperatorFormula> new_xs = new_x.searchOperators(x.getOperator());
                    new_xs.forEach(nx -> {if(nx.needSeparation()) xs.push((BinaryFormula) nx); });
                    updateRoot(x.replaceFormula(new_x));
                }
                else {
                    if(!areArranged(ljs) || !areArranged(rjs)){
                        ArrayList<OperatorFormula> new_xs = new ArrayList<>();
                        if(!areArranged(ljs)){
                            ljs.removeIf(Junction::isArranged);
                            if(ljs.iterator().hasNext()) {
                                Junction lj = ljs.iterator().next();
                                lj.arrange();
                                new_xs = x.searchOperators(x.getOperator());
                            }
                        }
                        if(!areArranged(rjs)){
                            rjs.removeIf(Junction::isArranged);
                            if(rjs.iterator().hasNext()) {
                                Junction rj = rjs.iterator().next();
                                rj.arrange();
                                new_xs = x.searchOperators(x.getOperator());
                            }
                        }
                        new_xs.forEach(nx -> {if(nx.needSeparation()) xs.push((BinaryFormula) nx); });
                    }
                }
            }
        }
        System.out.println("Separation performed.");

        return root;
    }

    /** Initializes the stack with the nodes of the formula that need to be separated.
     * It is used a stack to simulate a recursive function and to optimize in this way
     * the computations. */
    protected static Stack<BinaryFormula> initializeXStack(Formula phi) {
        Stack<BinaryFormula> stk = new Stack<>();
        Queue<Formula> q = new LinkedList<>();
        q.add(phi); // initialize the queue with the input node
        while(!q.isEmpty()) { // while the queue is not empty
            Formula f = q.remove();
            // if the formula f is not separated then
            if(f.needSeparation()) {
                // if f is an OperatorFormula then
                if(f instanceof OperatorFormula of) {
                    /* if f is a UnaryFormula then
                    add to the search queue its child */
                    if(of instanceof UnaryFormula uf) {
                        q.add(uf.getOperand());
                    }
                    /* if f is a BinaryFormula and need to be separated
                     * then if it has UNTIL or SINCE as operator that means
                     * it should be separated so add it to the stack  */
                    if(of instanceof BinaryFormula bf) {
                        if(bf.isOperator(UNTIL) || bf.isOperator(SINCE)) {
                            stk.add(bf);
                        }
                        /* add the two children to the search queue */
                        q.add(bf.getRoperand());
                        q.add(bf.getLoperand());
                    }
                }
            }
        }
        return stk;
    }

    private boolean isEliminable(BinaryFormula x, ArrayList<Junction> ljs, ArrayList<Junction> rjs) {
        if(ljs.isEmpty() && rjs.isEmpty()) return false;

        boolean needsLemmaA2WhereJunction = false;

        if(needsLemmaA2OR(x) && ljs.size() > 0) needsLemmaA2WhereJunction = true;
        if(needsLemmaA2AND(x) && rjs.size() > 0) needsLemmaA2WhereJunction = true;
        return (!needsLemmaA2WhereJunction && areArranged(ljs) && areArranged(rjs) && areOperatorChains(ljs) && areOperatorChains(rjs));
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
                /* if f is a UnaryFormula then add to the search queue its child */
                if(f instanceof UnaryFormula uf) q.add(uf.getOperand());
                /* if f is a BinaryFormula and needs to be separated
                 * then if it has UNTIL or SINCE as operator and
                 * its children are already separated then return f */
                if(f instanceof BinaryFormula bf) {
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
        return null;
    }

    public static OperatorFormula getNextOperator(OperatorFormula phi, Operator op) {
        Queue<Formula> q = new LinkedList<>();
        if(phi instanceof UnaryFormula uPhi) q.add(uPhi.getOperand());
        if(phi instanceof BinaryFormula bPhi) {
            q.add(bPhi.getLoperand());
            q.add(bPhi.getRoperand());
        }
        // while the queue is not empty repeat
        while(!q.isEmpty()) {
            Formula f = q.remove();
            if(f instanceof OperatorFormula of){
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
                        if(of instanceof UnaryFormula uf) q.add(uf.getOperand());
                        if(of instanceof BinaryFormula bf){
                            /* if f is a binary formula then perform the search on its operands,
                             * so they are added to the queue */
                            q.add(bf.getLoperand());
                            q.add(bf.getRoperand());
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Formula applyReversedLemmaA2(BinaryFormula f) {
        Operator operator = f.getOperator().equals(OR) ? OR : AND;
        ArrayList<Formula> operands = f.getCombinationOperands();
        ArrayList<Formula> newOperands = new ArrayList<>();

        operands.forEach(x -> {
            // if the newOperands list is empty then initialise it with x
            if(newOperands.isEmpty() && (x.isOperator(SINCE) || x.isOperator(UNTIL))) {
                newOperands.add(x);
            }
            else {
                if(x instanceof BinaryFormula bx && (bx.isOperator(SINCE) || bx.isOperator(UNTIL))) {
                    Iterator<Formula> newOperandsIterator = newOperands.iterator();
                    while (newOperandsIterator.hasNext()) {
                        Formula z = newOperandsIterator.next();
                        if (z instanceof BinaryFormula bz && needReversedLemmaA2(operator, bx, bz)) {
                            newOperands.add(reversedLemmaA2(operator, bx, bz));
                            newOperandsIterator.remove();
                        }
                        else newOperands.add(x);
                    }
                }
                else newOperands.add(x);
            }
        });

        return newCombination(operator, newOperands);
    }

}
