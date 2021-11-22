package separator;

import formula.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static formula.AtomConstant.TRUE;
import static formula.BinaryFormula.newCombination;
import static formula.BinaryFormula.newConjunction;
import static formula.BooleanRules.*;
import static formula.Operator.*;
import static separator.FormulaEliminator.FormulaEliminator.applyElimination;
import static separator.FormulaEliminator.FormulaEliminator.eliminationNumber;
import static separator.JunctionPath.junctionsList;
import static separator.Lemmas.*;
import static separator.Lemmata.needReversedLemmaA2;
import static separator.Lemmata.reversedLemmaA2;
import static simplifier.FormulaSimplifier.simplify;


public class FormulaSeparator {

    private Formula root;

    public void setRoot(Formula f) { root = f; }

    public Formula getRoot() { return root; }

    public void updateRoot(Formula f) {
        if(f.getParent() == null && f != root) setRoot(f);
    }

    private boolean needsNormalization(Formula f) {
        Queue<Formula> q = new LinkedList<>();
        q.add(f);
        while(!q.isEmpty()) {
            Formula nf = q.remove();
            if(nf instanceof OperatorFormula onf) {
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



    private Formula applyNormalizations(Formula f) {
        Queue<Formula> q = new LinkedList<>();
        q.add(f);
        while(!q.isEmpty()) {
            Formula nf = q.remove();
            if(nf instanceof OperatorFormula onf) {
                switch(onf.getOperator()) {
                    case NOT: {
                        UnaryFormula unf = (UnaryFormula) onf;
                        if(needsInvolution(unf)) {
                            f = unf.replaceFormula(involution(unf));
                            updateRoot(f);
                            q.add(f);
                            break;
                        }
                        if(needsDeMorganLaw(unf)) {
                            f = unf.replaceFormula(deMorganLaw(unf));
                            updateRoot(f);
                            q.add(f);
                            break;
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
                            f = bnf.replaceFormula(distributiveLaw(bnf));
                            updateRoot(f);
                            q.add(f);
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

    public Formula normalize() {
        while (needsNormalization(root)) {
            updateRoot(applyNormalizations(root));
        }
        return root;
    }


    /** @return Returns the matrix representing the formula f got in input */
    public PureFormulaeMatrix getPureFormulaeMatrix(Formula f) {

        System.out.println(f);

        PureFormulaeMatrix matrix = new PureFormulaeMatrix();
        ArrayList<Formula> conjunctions = getConjunctions(f);

        System.out.println(conjunctions.size());


        for (Formula c : conjunctions) {
            ArrayList<Formula> pastList = new ArrayList<>();
            ArrayList<Formula> presentList = new ArrayList<>();
            ArrayList<Formula> futureList = new ArrayList<>();
            ArrayList<Formula> pureFormulas = getPureFormulae(c);

            System.out.println(pureFormulas.size());

            for (Formula p : pureFormulas) {
                switch (p.getTime()){
                    case PAST -> pastList.add(p);
                    case PRESENT -> presentList.add(p);
                    case FUTURE -> futureList.add(p);
                }
            }

            if(pastList.size() == 0) pastList.add(new AtomicFormula(TRUE));
            if(presentList.size() == 0) presentList.add(new AtomicFormula(TRUE));
            if(futureList.size() == 0) futureList.add(new AtomicFormula(TRUE));

            matrix.addTriple(
                    newConjunction(pastList),
                    newConjunction(presentList),
                    newConjunction(futureList)
            );
        }
        return matrix;
    }

    /* pre:
     * - f is in DNF form;
     * - or is a pure formula;
     * - or a conjunction of pure formulas; */
    private ArrayList<Formula> getConjunctions(Formula f) {
        ArrayList<Formula> cnjs = new ArrayList<>();
        if(f.isOperator(OR)) cnjs = ((BinaryFormula) f).getCombinationOperands();
        else cnjs.add(f);
        return cnjs;
    }

    /*pre: f is atomic or binary */
    private ArrayList<Formula> getPureFormulae(Formula f) {
        ArrayList<Formula> pureFms = new ArrayList<>();
        if(f.isOperator(AND)) pureFms = ((BinaryFormula) f).getCombinationOperands();
        else pureFms.add(f);
        return pureFms;
    }




    /** Returns true if, and only if, all the junctions inside the passed
     * array list are arranged */
    private boolean areArranged(ArrayList<JunctionPath> js) {
        AtomicBoolean b = new AtomicBoolean(true);
        js.forEach(j -> { if(!j.isArranged()) b.set(false);});
        return b.get();
    }

    private boolean areOperatorChains(ArrayList<JunctionPath> js) {
        AtomicBoolean b = new AtomicBoolean(true);
        js.forEach(j -> { if(!j.isOperatorCombination()) b.set(false); });
        return b.get();
    }

    public static int junctionCase(JunctionPath j) {
        if(j == null) return 0;
        if(j.getY().getParent().isOperator(NOT)) return 2;
        if(j.getY().getParent().isOperator(j.getOperator())) return 1;
        return 0;
    }


    /** Separates the formula f got in input, which means that extracts the nested occurrences of U inside S and viceversa.
     * @return A Formula which is a combination of pure past, pure present and pure future formulas
     * @param f the formula which needs to be separated */
    public Formula separate(Formula f) throws ExecutionException, InterruptedException {
        root = f; // initialize the root with the formula f
        Stack<BinaryFormula> xs = initializeXStack(f); // initialize the stack of xs
        // while there is some x to separate
        //int i = 1;
        // i != 0 &&
        while (!xs.isEmpty()) {

            //i--;
            if(Thread.currentThread().isInterrupted()) break;
            Thread.sleep(100);
            BinaryFormula x = xs.pop(); // pop from the top of stack an x
            if(x.getLoperand() instanceof BinaryFormula lx) x.setLoperand(simplify(lx));
            if(x.getRoperand() instanceof BinaryFormula rx) x.setRoperand(simplify(rx));

            /* the ys nodes of the junctions in the left subtree starting from x */
            ArrayList<OperatorFormula> lys = x.getLoperand().searchOperators(x.getOperator().getMirrorOperator());
            /* the ys nodes of the junctions in the right subtree starting from x */
            ArrayList<OperatorFormula> rys = x.getRoperand().searchOperators(x.getOperator().getMirrorOperator());
            /* create a list containing the junctions in the left subtree of x */
            ArrayList<JunctionPath> ljs = junctionsList(x, lys);
            /* create a list containing the junctions in the right subtree of x */
            ArrayList<JunctionPath> rjs = junctionsList(x, rys);
            /* if the formula x has an eliminable form then */
            if(isEliminable(x, ljs, rjs)) {

                if(ljs.size() == 1) {
                    JunctionPath lj = ljs.iterator().next();
                    if(lj.isImmediateChild()) {lj.rewriteImmediateChild();}
                }
                if(rjs.size() == 1) {
                    JunctionPath rj = rjs.iterator().next();
                    if(rj.isImmediateChild()) {rj.rewriteImmediateChild();}
                }

                /*JunctionPath lj = null;
                JunctionPath rj = null;
                if(ljs.iterator().hasNext()) lj = ljs.iterator().next();
                if(rjs.iterator().hasNext()) rj = rjs.iterator().next();

                Formula found = applyElimination(lj, rj);*/


                ArrayList<JunctionPath[]> eliminationChoices = new ArrayList<>();
                ljs.forEach(lj -> rjs.forEach(rj -> eliminationChoices.add(new JunctionPath[] {lj, rj})));
                ljs.forEach(lj -> eliminationChoices.add(new JunctionPath[] {lj, null}));
                rjs.forEach(rj -> eliminationChoices.add(new JunctionPath[] {null, rj}));
                ExecutorService exec = Executors.newFixedThreadPool(1);
                List<Callable<Formula>> tasks = new ArrayList<>();
                eliminationChoices.forEach(j -> {
                    if(eliminationNumber(j[0], j[1])!= 0) tasks.add(
                            () -> {
                                Formula eliminatedPhi = applyElimination(j[0], j[1]);
                                SeparationTask separationTask = new SeparationTask(eliminatedPhi);
                                Formula taskResult = separationTask.call();
                                return taskResult;
                            });
                });

                Formula found = exec.invokeAny(tasks);

                exec.shutdown();
                try {
                    if (!exec.awaitTermination(10, TimeUnit.SECONDS)) {
                        exec.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    exec.shutdownNow();
                }

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
                            ljs.removeIf(JunctionPath::isArranged);
                            if(ljs.iterator().hasNext()) {
                                JunctionPath lj = ljs.iterator().next();
                                lj.arrange();
                                new_xs = x.searchOperators(x.getOperator());
                            }
                        }
                        if(!areArranged(rjs)){
                            rjs.removeIf(JunctionPath::isArranged);
                            if(rjs.iterator().hasNext()) {
                                JunctionPath rj = rjs.iterator().next();
                                rj.arrange();
                                new_xs = x.searchOperators(x.getOperator());
                            }
                        }
                        new_xs.forEach(nx -> {if(nx.needSeparation()) xs.push((BinaryFormula) nx); });
                    }
                }
            }
        }
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

    private boolean isEliminable(BinaryFormula x, ArrayList<JunctionPath> ljs, ArrayList<JunctionPath> rjs) {
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
