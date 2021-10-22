package formula;

import graphviz.GraphViz;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import static formula.Operator.*;
import static formula.TimeConstant.*;
import static formula.TypeConstant.*;

/** The Formula class represents a generic LTL formula. */
public abstract class Formula {

    /** The parent of the formula */
    private OperatorFormula parent;

    /** The type of the formula */
    private final TypeConstant type;

    /** The time of the formula, it is:
     *  PAST if, and only if, the formula is pure past;
     *  PRESENT if, and only if, the formula is pure present;
     *  FUTURE if, and only if, the formula is pure future;
     *  MIXED if, and only if, the formula is not pure. */
    private TimeConstant time;

    /** It is true if, and only, if the formula is separated, false otherwise. */
    private boolean separated;

    /** Initializes a newly created Formula with type t.
     * @param t the type of the formula */
    public Formula(TypeConstant t) {
        this.parent = null;
        this.type = t;
    }

    /** Initializes a newly created Formula with type t and parent p.
     * @param t the type of the formula
     * @param p the parent of the formula */
    public Formula(TypeConstant t, OperatorFormula p) {
        this.setParent(p);
        this.type = t;
    }

    /** Sets the parent of the formula
     * @param p the formula which will be set as parent */
    public void setParent(OperatorFormula p) {
        this.parent = p;
        if(p != null) {
            this.updateAncestorsTime();
            this.updateAncestorsSeparation();
        }
    }

    /** A setParent subroutine. Updates the time of the ancestors of the formula.
     * @see #setParent(OperatorFormula) */
    private void updateAncestorsTime() {
        Formula c = this;
        OperatorFormula p = c.getParent();
        while(p != null) {
            p.updateTime(c);
            c = p;
            p = p.getParent();
        }
    }

    /** A setParent subroutine. Updates the separation value of the ancestors of this.
     * @see #setParent(OperatorFormula) */
    private void updateAncestorsSeparation() {
        /* if the formula is not separated then
         * update the separation value of its ancestors */
        if(this.needSeparation()) {
            OperatorFormula p = this.getParent();
            while(p != null) {
                p.setSeparation(false);
                p = p.getParent();
            }
        }
        /* else if the formula is separated
         * update the separation value of its ancestors
         * on the basis of the operator time and of the
         * presence of the mirror operator inside the tree
         * rooted in it */
        else {
            OperatorFormula p = this.getParent();
            while(p != null) {
                p.updateSeparation();
                p = p.getParent();
            }
        }

    }

    public void debugSeparationPrint() {
        System.out.println("formula: " + this + " separated: " + this.getSeparation());
        if(this.isOperator()){
            OperatorFormula ot = (OperatorFormula) this;
            if(ot.isUnary()){
                UnaryFormula ut= (UnaryFormula) ot;
                ut.getOperand().debugSeparationPrint();
            }
            if(ot.isBinary()){
                BinaryFormula bt = (BinaryFormula) ot;
                System.out.println("lc separation: " + bt.getLoperand().getSeparation());
                System.out.println("rc separation: " + bt.getRoperand().getSeparation());
                bt.getLoperand().debugSeparationPrint();
                bt.getRoperand().debugSeparationPrint();
            }
        }
    }

    public void debugTimePrint() {
        System.out.println("formula: " + this + " time: " + this.getTime());
        if(this.isOperator()){
            OperatorFormula ot = (OperatorFormula) this;
            if(ot.isUnary()){
                UnaryFormula ut= (UnaryFormula) ot;
                ut.getOperand().debugTimePrint();
            }
            if(ot.isBinary()){
                BinaryFormula bt = (BinaryFormula) ot;
                System.out.println("op time:" + bt.getOperator().getTime());
                System.out.println("lc time:" + bt.getLoperand().getTime());
                System.out.println("rc time:" + bt.getRoperand().getTime());
                bt.getLoperand().debugTimePrint();
                bt.getRoperand().debugTimePrint();
            }
        }
    }

    /** @return Returns the parent of the formula */
    public OperatorFormula getParent(){ return this.parent; }

    /** @return Returns a TypeConstant which is type of the formula
     * @see TypeConstant*/
    public TypeConstant getType(){ return type; }

    /** @return Returns true if, and only if, the type of the formula is OPERATOR */
    public boolean isOperator() { return type == OPERATOR; }

    /** @return Returns true if, and only if, the type of the formula is OPERATOR and
     * its image corresponds to the image of the operator op */
    public boolean isOperator(Operator op){
        return this.isOperator() && Objects.equals(this.getImage(), op.getImage());
    }

    /** @return Returns true if, and only if, the type of the formula is ATOM */
    public boolean isAtomic() { return type == ATOM; }

    /** @return Returns a deep copy of the formula */
    public abstract Formula deepCopy();

    /** @return Returns the negation of the formula on which the method was called */
    public UnaryFormula negate() {return new UnaryFormula(NOT, this.deepCopy(), null); }

    /** @return Returns the string image of the formula */
    public abstract String getImage();

    /** @return Returns true if, and only if, this equals f */
    public abstract boolean equals(Formula f);

    /** @return Returns true if, and only if, this is the left child of the formula got in input
     * @param f the binary formula on which perform the check */
    public boolean isLeftChildOf(BinaryFormula f){
        return Objects.equals(this, f.getLoperand());
    }

    /** @return Returns true if, and only if, this is the right child of the formula got in input
     * @param f the BinaryFormula on which perform the check */
    public boolean isRightChildOf(BinaryFormula f){
        return Objects.equals(this, f.getRoperand());
    }

    /** @return Returns true if, and only if, this is one of the two children of the formula got in input
     * @param f the BinaryFormula on which perform the check */
    public boolean isChildOf(BinaryFormula f){
        return (this.isLeftChildOf(f) || this.isRightChildOf(f));
    }

    /** @return Returns true if, and only if, this is the child of the formula got in input
     * @param f the UnaryFormula on which perform the check */
    public boolean isChildOf(UnaryFormula f){
        return Objects.equals(this, f.getOperand());
    }

    /** @return Returns true if, and only if, this is in the right subtree of f */
    public boolean isInRightSubtreeOf(BinaryFormula f) {
        Formula nf = this;
        while(nf.getParent() != f && nf.getParent() != null) nf = nf.getParent();
        return (nf.getParent() != null && nf.getParent().equals(f) && nf.equals(f.getRoperand()));
    }

    /** @return Returns true if, and only if, this is in the left subtree of f */
    public boolean isInLeftSubtreeOf(BinaryFormula f){
        Formula nf = this;
        while(nf.getParent() != f && nf.getParent() != null) nf = nf.getParent();
        return (nf.getParent() != null && nf.getParent().equals(f) && nf.equals(f.getLoperand()));
    }


    public boolean isNestedInside(OperatorFormula f) {
        Formula t = this;
        while(t.getParent() != null && t.getParent() != f) t = t.getParent();
        return (t.getParent() != null && t.getParent().equals(f));
    }

    public void setTime(TimeConstant t){
        this.time = t;
    }

    public TimeConstant getTime() { return time; }

    public boolean isPure() {return time != MIXED; }

    public boolean isPure(TimeConstant t) { return time == t; }

    public void setSeparation(boolean b) { this.separated = b; }

    public boolean getSeparation() { return separated; }

    public boolean isSeparated(){ return separated; }

    public boolean needSeparation(){ return !separated; }

    public boolean isNestedInsideOperator(Operator op) {
        Formula nf = this;
        while(nf!= null && !nf.isOperator(op)){ nf = nf.getParent(); }
        if(nf != null) return nf.isOperator(op);
        return false;
    }

    /** Replaces the formula with another one.
     * @param f the formula which must take the place of the one on which the method was called
     * @return the formula which take the place of the old one */
    public Formula replaceFormula(Formula f) {
        OperatorFormula p = this.getParent();
        if(p == null) {
            f.setParent(null);
            return f;
        }
        if(p.isUnary()) {
            UnaryFormula up = (UnaryFormula) p;
            up.setOperand(f);
        }
        if(p.isBinary()) {
            BinaryFormula bp = (BinaryFormula) p;
            if(this.isLeftChildOf(bp)) bp.setLoperand(f);
            if(this.isRightChildOf(bp)) bp.setRoperand(f);
        }
        return f;
    }

    /** Searches among the ancestors of the formula a node whose
     * operator is the same of the parameter op.
     * @return an ancestor of the formula whose operator is op
     * or null if it is not present
     * @param op the operator on which the search is based on */
    public OperatorFormula searchAncestor(Operator op) {
        OperatorFormula np = this.getParent();
        while(np != null && !np.isOperator(op)) np = np.getParent();
        return np;
    }

    public OperatorFormula searchFirstTempAncestor() {
        OperatorFormula np = this.getParent();
        while(np != null && !np.isOperator(UNTIL) && !np.isOperator(SINCE)) np = np.getParent();
        return np;
    }


    public boolean containsOperatorOfTime(TimeConstant t) {
        Queue<Formula> q = new LinkedList<>();
        q.add(this);
        while(!q.isEmpty()) {
            Formula f = q.remove();
            /* if the formula is pure PAST, which means that contains
             * at least a PAST operator, then return true */
            if(f.isPure(t)) return true;
            /* if f is an OperatorFormula and is not pure (MIXED),
             * it might contain a PAST operator */
            if(f.isOperator() && !f.isPure()) {
                OperatorFormula of = (OperatorFormula) f;
                /* if the operator of the formula has time t then
                 * return true (this is the case when the method is
                 * called on a formula that has as operator the searched one) */
                if(of.getOperator().getTime().equals(t)) {
                    return true;
                }
                /* if it is a UnaryFormula then continue the search
                 * on its child */
                if(of.isUnary()) {
                    UnaryFormula uf = (UnaryFormula) of;
                    q.add(uf.getOperand());
                }
                /* if it is a UnaryFormula then continue the search
                 * on its two children */
                if(of.isBinary()) {
                    BinaryFormula bf = (BinaryFormula) of;
                    if(bf.getLoperand() != null) q.add(bf.getLoperand());
                    if(bf.getRoperand() != null) q.add(bf.getRoperand());
                }
            }
        }
        return false;
    }


    /** Performs a BFS of a formula whose top operator is op.
     * @return Returns if found an OperatorFormula whose top operator is op
     * else return null
     * @param op the Operator to be searched */
    public OperatorFormula searchOperator(Operator op) {
        Queue<Formula> q = new LinkedList<>();
        q.add(this);
        while(!q.isEmpty()) {
            Formula f = q.remove();
            if(f.isOperator()) {
                OperatorFormula of = (OperatorFormula) f;
                if(of.isOperator(op)) return of;
                switch (op.getTime()) {
                    case PAST : {
                        switch (of.getTime()) {
                            case PAST, MIXED: {
                                if(of.isUnary()) {
                                    UnaryFormula uf = (UnaryFormula) of;
                                    q.add(uf.getOperand());
                                }
                                if(of.isBinary()) {
                                    BinaryFormula bf = (BinaryFormula) of;
                                    q.add(bf.getLoperand());
                                    q.add(bf.getRoperand());
                                }
                                break;
                            }
                            default: break;
                        }
                        break;
                    }
                    case PRESENT: {
                        if(of.isUnary()) {
                            UnaryFormula uf = (UnaryFormula) of;
                            q.add(uf.getOperand());
                        }
                        if(of.isBinary()) {
                            BinaryFormula bf = (BinaryFormula) of;
                            q.add(bf.getLoperand());
                            q.add(bf.getRoperand());
                        }
                        break;
                    }
                    case FUTURE:  {
                        switch (op.getTime()) {
                            case FUTURE, MIXED: {
                                if(of.isUnary()) {
                                    UnaryFormula uf = (UnaryFormula) of;
                                    q.add(uf.getOperand());
                                }
                                if(of.isBinary()) {
                                    BinaryFormula bf = (BinaryFormula) of;
                                    q.add(bf.getLoperand());
                                    q.add(bf.getRoperand());
                                }
                                break;
                            }
                            default: break;
                        }
                        break;
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<OperatorFormula> searchOperators(Operator op) {
        ArrayList<OperatorFormula> r = new ArrayList<>();
        Queue<Formula> q = new LinkedList<>();
        q.add(this);
        while(!q.isEmpty()) {
            Formula f = q.remove();
            if(f.isOperator()) {
                OperatorFormula of = (OperatorFormula) f;
                if(of.isOperator(op)) r.add(of);
                else switch (op.getTime()) {
                    case PAST : {
                        switch (of.getTime()) {
                            case PAST, MIXED: {
                                if(of.isUnary()) {
                                    UnaryFormula uf = (UnaryFormula) of;
                                    q.add(uf.getOperand());
                                }
                                if(of.isBinary()) {
                                    BinaryFormula bf = (BinaryFormula) of;
                                    q.add(bf.getLoperand());
                                    q.add(bf.getRoperand());
                                }
                                break;
                            }
                            default: break;
                        }
                        break;
                    }
                    case PRESENT: {
                        if(of.isUnary()) {
                            UnaryFormula uf = (UnaryFormula) of;
                            q.add(uf.getOperand());
                        }
                        if(of.isBinary()) {
                            BinaryFormula bf = (BinaryFormula) of;
                            q.add(bf.getLoperand());
                            q.add(bf.getRoperand());
                        }
                        break;
                    }
                    case FUTURE:  {
                        switch (op.getTime()) {
                            case FUTURE, MIXED: {
                                if(of.isUnary()) {
                                    UnaryFormula uf = (UnaryFormula) of;
                                    q.add(uf.getOperand());
                                }
                                if(of.isBinary()) {
                                    BinaryFormula bf = (BinaryFormula) of;
                                    q.add(bf.getLoperand());
                                    q.add(bf.getRoperand());
                                }
                                break;
                            }
                            default: break;
                        }
                        break;
                    }
                }
            }
        }
        return r;
    }

    public boolean isNegationOf(Formula f) {
        return this.equals(BooleanRules.negate(f.deepCopy()));
    }

    public Formula searchSameOperatorFormula(OperatorFormula phi) {
        ArrayList<OperatorFormula> al = this.searchOperators(phi.getOperator());
        for (OperatorFormula f : al) {
            if(f != phi) return f;
        }
        return null;
    }


    /** Translates a formula into an object of the GraphViz class.
     * @return Returns the GraphViz representation of the formula f
     * @see GraphViz */
    public GraphViz fromFormulaToGraphViz() {
        GraphViz gv = new GraphViz();
        gv.add("graph G {");
        if(this.isAtomic()) {
            gv.addln(this.getImage() + ";");
        }
        if(this.isOperator()) {
            operatorFormulaGraphViz(gv, (OperatorFormula) this);
        }
        gv.addln(gv.end_graph());
        return gv;
    }

    /** A fromFormulaToGraphViz subroutine.
     * @see #fromFormulaToGraphViz()
     * @see GraphViz */
    private static void operatorFormulaGraphViz(GraphViz gv, OperatorFormula f){
        if(f.isUnary()) {
            Formula c = ((UnaryFormula) f).getOperand();
            gv.addln(f.hashCode() + " [label=\"" + f.getImage() + "\"]" + ";");
            gv.addln(c.hashCode() + " [label=\"" + c.getImage() + "\"]" + ";");
            gv.addln(f.hashCode() + "--" + c.hashCode() + ";");
            if(c.isOperator()) operatorFormulaGraphViz(gv, (OperatorFormula) c);
        }
        if(f.isBinary()) {
            Formula lc = ((BinaryFormula) f).getLoperand();
            Formula rc = ((BinaryFormula) f).getRoperand();
            gv.addln(f.hashCode() + " [ " +  nodeDebugColor(f) + "label=\""  + f.getImage() + "\"]" + ";");
            gv.addln(lc.hashCode() + " [ " + nodeDebugColor(lc) + "label=\""  + lc.getImage() + "\"]" + ";");
            gv.addln(rc.hashCode() +" [ " + nodeDebugColor(rc) + "label=\"" +  rc.getImage() + "\"]" + ";");
            gv.addln(f.hashCode() + "--" + lc.hashCode() + ";");
            if(lc.isOperator()) operatorFormulaGraphViz(gv, (OperatorFormula) lc);
            gv.addln(f.hashCode() + "--" + rc.hashCode() + ";");
            if(rc.isOperator()) operatorFormulaGraphViz(gv, (OperatorFormula) rc);
        }
    }

    /** An operatorFormulaGraphViz subroutine.
     * @see #operatorFormulaGraphViz(GraphViz, OperatorFormula)
     * @see GraphViz*/
    private static String nodeDebugColor(Formula f){
        if(f.isOperator(SINCE) || f.isOperator(UNTIL)){
            BinaryFormula bf = (BinaryFormula) f;
            if(bf.isNestedInsideMirror()) return "color=red,";
            if(f.searchOperator(bf.getOperator().getMirrorOperator()) != null) return "color=blue,";
        }
        return "";
    }



}
