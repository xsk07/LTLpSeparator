package separator;

import formula.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import static formula.AtomConstant.TRUE;
import static formula.BinaryFormula.newConjunction;
import static formula.BooleanRules.*;
import static formula.Operator.AND;
import static formula.Operator.OR;
import static separator.FormulaSimplifier.simplify;

public class FormulaNormalizer {

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

    public Formula normalize(Formula f) {
        root = f;
        while (needsNormalization(root)) {
            updateRoot(applyNormalizations(root));
        }
        if(root instanceof BinaryFormula br) root = simplify(br);
        return root;
    }

    /** @return Returns the matrix representing the formula f got in input */
    public PureFormulaeMatrix getPureFormulaeMatrix(Formula f) {
        PureFormulaeMatrix matrix = new PureFormulaeMatrix();
        ArrayList<Formula> conjunctions = getConjunctions(f);

        for (Formula c : conjunctions) {
            ArrayList<Formula> pastList = new ArrayList<>();
            ArrayList<Formula> presentList = new ArrayList<>();
            ArrayList<Formula> futureList = new ArrayList<>();

            ArrayList<Formula> pureFormulae = getPureFormulae(c);

            for (Formula p : pureFormulae) {
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
        if(f.isOperator(OR)) {
            BinaryFormula bf = (BinaryFormula) f;
            cnjs = bf.getCombinationOperands();
        }
        else cnjs.add(f);
        return cnjs;
    }

    /*pre: f is atomic or binary */
    private ArrayList<Formula> getPureFormulae(Formula f) {
        ArrayList<Formula> pureFms = new ArrayList<>();
        if(f.isOperator(AND)) {
            BinaryFormula bf = (BinaryFormula) f;
            pureFms = bf.getCombinationOperands();
        }
        else pureFms.add(f);
        return pureFms;
    }


}
