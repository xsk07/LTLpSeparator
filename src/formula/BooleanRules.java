package formula;

import static formula.Operator.*;

public abstract class BooleanRules {

    /** @return Returns the negation of the formula on which the method was called */
    public UnaryFormula negate(Formula f) {
        return new UnaryFormula(NOT, f.deepCopy(), null);
    }

    public static Formula deMorganLaw(UnaryFormula f) {
        if(f.isOperator(NOT)) {
            if(f.getOperand().isOperator(AND)) {
                BinaryFormula fOp = (BinaryFormula) f.getOperand();
                fOp.setOperator(OR);
                fOp.setLoperand(fOp.getLoperand().negate());
                fOp.setRoperand(fOp.getRoperand().negate());
            }
            if(f.getOperand().isOperator(OR)) {
                BinaryFormula op_f = (BinaryFormula) f.getOperand();
                op_f.setOperator(AND);
                op_f.setLoperand(op_f.getLoperand().negate());
                op_f.setRoperand(op_f.getRoperand().negate());
            }
            System.out.println("DeMorganRule");
            return f.negate();
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should start with a NOT operator but starts with %s",
                        f.getOperator().getImage()
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
