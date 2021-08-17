package translator;
import formula.*;
import parser.SimpleNode;
import static formula.Operator.*;

public class Translator {

    /** Translates an AST, consisting of the instances of the SimpleNode class,
     * into an equivalent instance of the Formula class.
     * @param n A SimpleNode which is the root of the AST to translate
     * @return Returns a formula which is the translated form of the AST on which the method was called
     * @see parser.Node
     * @see parser.SimpleNode
     * @see formula.Formula */
    public static Formula fromSimpleNodeToFormula(SimpleNode n) throws IllegalArgumentException {
        int nId = n.getId();
        switch(nId){
            case 0: { //INPUT
                SimpleNode c = (SimpleNode) n.jjtGetChild(0); // child node of n
                /* Jump the Input node and return, as the root of the formula, the
                formula translation of its child */
                return fromSimpleNodeToFormula(c);
            }
            /* ALL BINARY */
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: return translateBinaryNode(n);
            case 7: { //UNARY
                SimpleNode c = (SimpleNode) n.jjtGetChild(0);
                String img = (String) n.jjtGetValue();
                UnaryFormula f = new UnaryFormula(fromString(img));
                Formula uOp = fromSimpleNodeToFormula(c);
                f.setOperand(uOp);
                return f;
            }
            case 8: { //ATOM
                /* return an atomic formula with the same image of the node n */
                String img = (String) n.jjtGetValue(); // node image
                return new AtomicFormula(img);
            }
            default: throw new IllegalArgumentException(
                    String.format("The node should have an id value between 0 and 8, but it has %s", nId)
            );
        }
    }

    /** A fromSimpleNodeToFormula subroutine.
     * Translates a SimpleNode, with an id corresponding to a binary operator,
     * into an equivalent formula.
     * @param n The node to translate
     * @return Returns the form of the node into a equivalent formula form
     * @see #fromSimpleNodeToFormula(SimpleNode)
     * */
    private static Formula translateBinaryNode(SimpleNode n) {
        SimpleNode lc = (SimpleNode) n.jjtGetChild(0); // left child of n
        SimpleNode rc = (SimpleNode) n.jjtGetChild(1); // right child of n
        Operator op = fromString((String) n.jjtGetValue());
        BinaryFormula f = new BinaryFormula(op); // root formula
        Formula lOp = fromSimpleNodeToFormula(lc); // translation of the left child
        Formula rOp = fromSimpleNodeToFormula(rc); // translation of the right child
        f.setLoperand(lOp); // set the left child
        f.setRoperand(rOp); // set the right child
        return f;
    }

}