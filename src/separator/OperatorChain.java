package separator;

import formula.BinaryFormula;
import formula.Formula;
import formula.Operator;
import formula.UnaryFormula;
import static formula.Operator.NOT;

public abstract class OperatorChain {

    /** Search, inside a chain of operators of the same type, the operator op.
     * @param f The node on which start the search
     * @param op The operator to be search inside the operator chain
     * @return Returns a formula for which the operator corresponds to op */
    public static Formula operatorChainSearch(BinaryFormula f, Operator op) {

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

    public static Formula operatorChainSearchOfNegation(BinaryFormula f, Operator op){
        /* if the right child operator of f corresponds to the negation of the operator op then
        return the left child of f */
        if(f.getRoperand().isOperator(NOT)) {
            UnaryFormula rf = (UnaryFormula) f.getRoperand();
            if(rf.getOperand().isOperator(op)) return rf;
        }

        /* if the left child operator of f corresponds to the operator op then
        return the left child of f */
        if(f.getLoperand().isOperator(NOT)){
            UnaryFormula lf = (UnaryFormula) f.getLoperand();
            if(lf.getOperand().isOperator(op)) return lf;

        }

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


    /** Rearranges the chain of operators starting at f so that the formula with operator op is the right child of f.
     * @param f The starting node of the operator chain
     * @param op The operator of the formula that should be moved */
    public static void setupOperatorChain(BinaryFormula f, Operator op){

        /* Do the search of the operator op inside the operator chain starting at node f */
        Formula searchedOperator = operatorChainSearch(f, op);

        /* if the formula with operator op was found then */
        if(searchedOperator != null) {
            rearrangeInnerFormula(f, searchedOperator);
        }
    }

    public static void rearrangeInnerFormula(BinaryFormula f, Formula sf) {

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



}
