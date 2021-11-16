package formula;

import graphviz.GraphViz;
import parser.SimpleNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import static formula.Operator.*;
import static formula.TimeConstant.*;

/** The Formula class represents a generic LTL formula. */
public abstract class Formula {

    /** The parent of the formula */
    private OperatorFormula parent;

    /** The time of the formula, it is:
     *  PAST if, and only if, the formula is pure past;
     *  PRESENT if, and only if, the formula is pure present;
     *  FUTURE if, and only if, the formula is pure future;
     *  MIXED if, and only if, the formula is not pure. */
    private TimeConstant time;

    /** It is true if, and only, if the formula is separated, false otherwise. */
    private boolean separated;

    /** Initializes a newly created Formula with type t.*/
    public Formula() {
        this.parent = null;
    }

    /** Initializes a newly created Formula with type t and parent p.
     * @param p the parent of the formula */
    public Formula(OperatorFormula p) {
        this.setParent(p);
    }

    public static Formula parseTreeToFormula(SimpleNode parseTree) {
        if(parseTree.getId() == 0) return fromSimpleNodeToFormula(parseTree);
        throw new IllegalArgumentException("The parameter must be the root node of the parse tree");
    }

    /** Translates a parse tree, consisting of an instance of the SimpleNode class,
     * into an instance of the Formula class.
     * @return Returns a formula which is the translated form of the parse tree on which the method was called
     * @see formula.Formula */
    private static Formula fromSimpleNodeToFormula(SimpleNode node) throws IllegalArgumentException {

        switch(node.getId()) {
            case 0: { //INPUT
                /* Jump the Input node and return, as the root of the formula, the
                formula translation of its unique child */
                SimpleNode c = (SimpleNode) node.jjtGetChild(0);
                return fromSimpleNodeToFormula(c);
            }
            // ALL BINARY
            case 2:
            case 3:
            case 4:
            case 5: return translateBinaryNode(node);
            case 6: {
                BinaryFormula bf = translateBinaryNode(node);
                bf.swapChildren();
                return bf;
            }
            case 7: { //UNARY
                SimpleNode c = (SimpleNode) node.jjtGetChild(0);
                String img = node.jjtGetValue();
                /* the input formulas are expressed in infix notation
                 * while the program logic uses the prefix one,
                 * hence to preserve inside the program the same meaning
                 * of the input formulae it is needed to swap the two children */
                return new UnaryFormula(
                        fromString(img),
                        fromSimpleNodeToFormula(c)
                );
            }
            case 8: { //ATOM
                /* return an atomic formula with the same image of the node n */
                return new AtomicFormula(node.jjtGetValue());
            }
            default: throw new IllegalArgumentException(
                    String.format("The node should have an id value between 0 and 8, but it has %s", node.getId())
            );
        }
    }

    /** A fromSimpleNodeToFormula subroutine.
     * Translates a SimpleNode, with an id corresponding to a binary operator,
     * into an equivalent formula.
     * @param n The SimpleNode to translate
     * @return Returns the translation of the node into a node of the formula AST
     * @see #fromSimpleNodeToFormula(SimpleNode)
     * */
    private static BinaryFormula translateBinaryNode(SimpleNode n) {
        SimpleNode lc = (SimpleNode) n.jjtGetChild(0); // left child of n
        SimpleNode rc = (SimpleNode) n.jjtGetChild(1); // right child of n
        Operator op = fromString(n.jjtGetValue());
        return new BinaryFormula(
                op,
                fromSimpleNodeToFormula(lc), // translation of the left child
                fromSimpleNodeToFormula(rc)  // translation of the right child
        );
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
        if(this instanceof UnaryFormula uThis) uThis.getOperand().debugSeparationPrint();
        if(this instanceof BinaryFormula bThis){
            System.out.println("lc separation: " + bThis.getLoperand().getSeparation());
            System.out.println("rc separation: " + bThis.getRoperand().getSeparation());
            bThis.getLoperand().debugSeparationPrint();
            bThis.getRoperand().debugSeparationPrint();
        }
    }

    public void debugTimePrint() {
        System.out.println("formula: " + this + " time: " + this.getTime());
        if(this instanceof UnaryFormula ut) ut.getOperand().debugTimePrint();
        if(this instanceof BinaryFormula bt){
            System.out.println("op time:" + bt.getOperator().getTime());
            System.out.println("lc time:" + bt.getLoperand().getTime());
            System.out.println("rc time:" + bt.getRoperand().getTime());
            bt.getLoperand().debugTimePrint();
            bt.getRoperand().debugTimePrint();
        }
    }

    public abstract boolean equalTo(Formula f);

    /** @return Returns the parent of the formula */
    public OperatorFormula getParent(){ return this.parent; }


    /** @return Returns true if, and only if, the type of the formula is OPERATOR and
     * its image corresponds to the image of the operator op */
    public boolean isOperator(Operator op){
        return this instanceof OperatorFormula && Objects.equals(this.getImage(), op.getImage());
    }

    /** @return Returns a deep copy of the formula */
    public abstract Formula deepCopy();

    /** @return Returns the negation of the formula on which the method was called */
    public UnaryFormula negate() {return new UnaryFormula(NOT, this.deepCopy(), null); }

    /** @return Returns the string image of the formula */
    public abstract String getImage();

    /** @return Returns true if, and only if, this is the left child of the formula got in input
     * @param f the binary formula on which perform the check */
    public boolean isLeftChildOf(BinaryFormula f){
        return this == f.getLoperand();
    }

    /** @return Returns true if, and only if, this is the right child of the formula got in input
     * @param f the BinaryFormula on which perform the check */
    public boolean isRightChildOf(BinaryFormula f){
        return this == f.getRoperand();
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
        return (nf.getParent() != null && nf.getParent() == f && nf == f.getRoperand());
    }

    /** @return Returns true if, and only if, this is in the left subtree of f */
    public boolean isInLeftSubtreeOf(BinaryFormula f){
        Formula nf = this;
        while(nf.getParent() != f && nf.getParent() != null) nf = nf.getParent();
        return (nf.getParent() != null && nf.getParent() == f && nf == f.getLoperand());
    }


    public boolean isNestedInside(OperatorFormula f) {
        Formula t = this;
        while(t.getParent() != null && t.getParent() != f) t = t.getParent();
        return (t.getParent() != null && t.getParent() == f);
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


    /** Replaces the formula with another one.
     * @param f the formula which must take the place of the one on which the method was called
     * @return the formula which take the place of the old one */
    public Formula replaceFormula(Formula f) {
        OperatorFormula p = this.getParent();
        if(p == null) {
            f.setParent(null);
            return f;
        }
        if(p instanceof UnaryFormula up) up.setOperand(f);

        if(p instanceof BinaryFormula bp) {
            if(this.isLeftChildOf(bp)) bp.setLoperand(f);
            if(this.isRightChildOf(bp)) bp.setRoperand(f);
        }
        return f;
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
            if(f instanceof OperatorFormula of && !f.isPure()) {
                /* if the operator of the formula has time t then
                 * return true (this is the case when the method is
                 * called on a formula that has as operator the searched one) */
                if(of.getOperator().getTime().equals(t)) {
                    return true;
                }
                /* if it is a UnaryFormula then continue the search
                 * on its child */
                if(of instanceof UnaryFormula uf) {
                    q.add(uf.getOperand());
                }
                /* if it is a UnaryFormula then continue the search
                 * on its two children */
                if(of instanceof BinaryFormula bf) {
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
            if(f instanceof OperatorFormula of) {
                if(of.isOperator(op)) return of;
                switch (op.getTime()) {
                    case PAST : {
                        switch (of.getTime()) {
                            case PAST, MIXED: {
                                if(of instanceof UnaryFormula uf) {
                                    q.add(uf.getOperand());
                                }
                                if(of instanceof BinaryFormula bf) {
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
                        if(of instanceof UnaryFormula uf) {
                            q.add(uf.getOperand());
                        }
                        if(of instanceof BinaryFormula bf) {
                            q.add(bf.getLoperand());
                            q.add(bf.getRoperand());
                        }
                        break;
                    }
                    case FUTURE:  {
                        switch (op.getTime()) {
                            case FUTURE, MIXED: {
                                if(of instanceof UnaryFormula uf) {
                                    q.add(uf.getOperand());
                                }
                                if(of instanceof BinaryFormula bf) {
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
            if(f instanceof OperatorFormula of) {
                if(of.isOperator(op)) r.add(of);
                else switch (op.getTime()) {
                    case PAST : {
                        switch (of.getTime()) {
                            case PAST, MIXED: {
                                if(of instanceof UnaryFormula uf) {
                                    q.add(uf.getOperand());
                                }
                                if(of instanceof BinaryFormula bf) {
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
                        if(of instanceof UnaryFormula uf) {
                            q.add(uf.getOperand());
                        }
                        if(of instanceof BinaryFormula bf) {
                            q.add(bf.getLoperand());
                            q.add(bf.getRoperand());
                        }
                        break;
                    }
                    case FUTURE:  {
                        switch (op.getTime()) {
                            case FUTURE, MIXED: {
                                if(of instanceof UnaryFormula uf) {
                                    q.add(uf.getOperand());
                                }
                                if(of instanceof BinaryFormula bf) {
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


    public static boolean opposite(Formula f1, Formula f2) {
        return f1.isNegationOf(f2) || f2.isNegationOf(f1);
    }

    public boolean isNegationOf(Formula f) {
        if(this instanceof UnaryFormula ut) return ut.getOperand().equalTo(f);
        return false;
    }

    /** Translates a formula into an object of the GraphViz class.
     * @return Returns the GraphViz representation of the formula f
     * @see GraphViz */
    public GraphViz fromFormulaToGraphViz() {
        GraphViz gv = new GraphViz();
        gv.add("graph G {");
        if(this instanceof AtomicFormula) {
            gv.addln(this.getImage() + ";");
        }
        if(this instanceof OperatorFormula ot) {
            operatorFormulaGraphViz(gv, ot);
        }
        gv.addln(gv.end_graph());
        return gv;
    }

    /** A fromFormulaToGraphViz subroutine.
     * @see #fromFormulaToGraphViz()
     * @see GraphViz */
    private static void operatorFormulaGraphViz(GraphViz gv, OperatorFormula f){
        if(f instanceof UnaryFormula uf) {
            Formula c = uf.getOperand();
            gv.addln(f.hashCode() + " [label=\"" + f.getImage() + "\"]" + ";");
            gv.addln(c.hashCode() + " [label=\"" + c.getImage() + "\"]" + ";");
            gv.addln(f.hashCode() + "--" + c.hashCode() + ";");
            if(c instanceof OperatorFormula oc) operatorFormulaGraphViz(gv, oc);
        }
        if(f instanceof BinaryFormula bf) {
            Formula lc = bf.getLoperand();
            Formula rc = bf.getRoperand();
            gv.addln(f.hashCode() + " [ " +  nodeDebugColor(f) + "label=\""  + f.getImage() + "\"]" + ";");
            gv.addln(lc.hashCode() + " [ " + nodeDebugColor(lc) + "label=\""  + lc.getImage() + "\"]" + ";");
            gv.addln(rc.hashCode() +" [ " + nodeDebugColor(rc) + "label=\"" +  rc.getImage() + "\"]" + ";");
            gv.addln(f.hashCode() + "--" + lc.hashCode() + ";");
            if(lc instanceof OperatorFormula olc) operatorFormulaGraphViz(gv, olc);
            gv.addln(f.hashCode() + "--" + rc.hashCode() + ";");
            if(rc instanceof OperatorFormula orc) operatorFormulaGraphViz(gv, orc);
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
