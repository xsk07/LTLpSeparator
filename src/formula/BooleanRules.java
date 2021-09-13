package formula;

import static formula.Operator.*;
import static separator.OperatorChain.operatorChainSearch;
import static separator.OperatorChain.operatorChainSearchOfNegation;

public abstract class BooleanRules {

    /** @return Returns the negation of the formula on which the method was called */
    public UnaryFormula negate(Formula f) {
        return new UnaryFormula(NOT, f.deepCopy(), null);
    }

    public static boolean needsInvolution(UnaryFormula f){
        if(f.isOperator(NOT)){
            return (f.getOperand().isOperator(NOT));
        }
        throw new IllegalArgumentException(
                String.format(
                        "The operator of the formula should be a NOT but is %s",
                        f.getOperator().getImage()
                )
        );
    }

    public static Formula involution(UnaryFormula f) {

        if(f.isOperator(NOT)) {
            if(f.getOperand().isOperator(NOT)){
                UnaryFormula cf = (UnaryFormula) f.getOperand();
                return cf.getOperand();
            }
            return f;
        }
        throw new IllegalArgumentException(
                String.format(
                        "The operator of the formula should be a NOT but is %s",
                        f.getImage()
                )
        );

    }

    public static boolean needsDeMorganLaw(BinaryFormula f) {

        if(f.getLoperand().isOperator(NOT)){
            UnaryFormula lf = (UnaryFormula) f.getLoperand();
            if(lf.getOperand().isOperator(OR)){
                if(operatorChainSearch(
                        (BinaryFormula) lf.getOperand(),
                        f.getOperator().getMirrorOperator()
                ) != null) return true;
                if(operatorChainSearchOfNegation(
                        (BinaryFormula) lf.getOperand(),
                        f.getOperator().getMirrorOperator()
                ) != null) return true;
            }
        }
        if(f.getRoperand().isOperator(NOT)){
            UnaryFormula rf = (UnaryFormula) f.getRoperand();
            if(rf.getOperand().isOperator(AND)){
                if (operatorChainSearch(
                        (BinaryFormula) rf.getOperand(),
                        f.getOperator().getMirrorOperator()
                ) != null) return true;
                if(operatorChainSearchOfNegation(
                        (BinaryFormula) rf.getOperand(),
                        f.getOperator().getMirrorOperator()
                ) != null) return true;
            }
        }
        return false;
    }



    public static Formula deMorganLaw(BinaryFormula f, UnaryFormula cf) {

        if(cf.isOperator(NOT)) {

            if(cf.getOperand().isOperator(OR) && f.isLeftChild(cf)) {
                BinaryFormula op_f = (BinaryFormula) cf.getOperand();
                op_f.setOperator(AND);
                op_f.setLoperand(op_f.getLoperand().negate());
                op_f.setRoperand(op_f.getRoperand().negate());
                System.out.println("DeMorganLaw");
                return op_f;
            }

            if(cf.getOperand().isOperator(AND) && f.isRightChild(cf)) {
                BinaryFormula fOp = (BinaryFormula) cf.getOperand();
                fOp.setOperator(OR);
                fOp.setLoperand(fOp.getLoperand().negate());
                fOp.setRoperand(fOp.getRoperand().negate());
                System.out.println("DeMorganLaw");
                return fOp;
            }

            return cf.negate();
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should start with a NOT operator but starts with %s",
                        cf.getOperator().getImage()
                )
        );
    }



    public static BinaryFormula distributionRule(BinaryFormula f){

        if(!(f.isOperator(AND) || f.isOperator(OR))) {
            throw new IllegalArgumentException(
                    String.format("The distribution rule can be only applied to formulas that have an AND/OR operator but the formula operator is: %s", f.getOperator())
            );
        }
        if(!f.getRoperand().isOperator(f.getOperator().getMirrorOperator())){
            throw new IllegalArgumentException(
                    String.format("The operator of the right operand should be %s but is %s", f.getOperator().getMirrorOperator(), f.getImage())
            );
        }

        BinaryFormula rf = (BinaryFormula) f.getRoperand();

        System.out.println("DistributionRule");
        return new BinaryFormula(
                f.getOperator().getMirrorOperator(),
                new BinaryFormula(
                        f.getOperator(),
                        f.getLoperand().deepCopy(),
                        rf.getLoperand().deepCopy()
                ),
                new BinaryFormula(
                        f.getOperator(),
                        f.getLoperand().deepCopy(),
                        rf.getRoperand().deepCopy()
                )
        );




    }







}
