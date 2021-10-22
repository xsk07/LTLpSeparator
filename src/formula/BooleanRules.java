package formula;

import static formula.AtomConstant.*;
import static formula.Operator.*;
import static separator.OperatorChain.operatorChainSearch;
import static separator.OperatorChain.operatorChainSearchOfNegation;

public abstract class BooleanRules {

    /** @return Returns the negation of the formula on which the method was called */
    public static UnaryFormula negate(Formula f) { return new UnaryFormula(NOT, f, null); }

    public static boolean needTruthValueNegation(UnaryFormula f){
        if(f.isOperator(NOT)){
            if(f.getOperand().isAtomic()){
                AtomicFormula cf = (AtomicFormula) f.getOperand();
                return (cf.isTruthValue());
            }
        }
        return false;
    }

    public static Formula truthValueNegation(UnaryFormula f) {
        if(f.isOperator(NOT)){
            if(f.getOperand().isAtomic()){
                AtomicFormula cf = (AtomicFormula) f.getOperand();
                if(cf.getImage().equals(TRUE.getImage())) {
                    return new AtomicFormula(FALSE);
                }
                if(cf.getImage().equals(FALSE.getImage())) {
                    return new AtomicFormula(TRUE);
                }
            }
        }
        return f;
    }

    public static BinaryFormula implicationRule(BinaryFormula f) {
        return new BinaryFormula(
                OR,
                f.getLoperand().negate(),
                f.getRoperand()
        );
    }

    public static BinaryFormula equivalenceRule(BinaryFormula f) {
        return new BinaryFormula(
                OR,
                new BinaryFormula(
                        AND,
                        f.getLoperand().deepCopy(),
                        f.getRoperand().deepCopy()
                ),
                new BinaryFormula(
                        AND,
                        f.getLoperand().deepCopy().negate(),
                        f.getRoperand().deepCopy().negate()
                )
        );
    }

    public static boolean needsInvolution(UnaryFormula f) {
        if(!f.isOperator(NOT)){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula should be a NOT but is %s",
                            f.getOperator().getImage()
                    )
            );
        }
        return f.getOperand().isOperator(NOT);
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

    public static boolean needsDeMorganLaw(UnaryFormula f) {
        if(!f.isOperator(NOT)){
            throw new IllegalArgumentException (
                    String.format(
                            "The formula should start with a NOT operator but starts with %s",
                            f.getOperator().getImage()
                    )
            );
        }
        return (f.getOperand().isOperator(AND) || f.getOperand().isOperator(OR));
    }

    public static Formula deMorganLaw(UnaryFormula f) {

        if(f.isOperator(NOT)) {

            if(f.getOperand().isOperator(OR) || f.getOperand().isOperator(AND)) {
                BinaryFormula bf = (BinaryFormula) f.getOperand();
                assert bf.getOperator().getMirrorOperator() != null;
                bf.setOperator(bf.getOperator().getMirrorOperator());
                bf.setLoperand(negate(bf.getLoperand()));
                bf.setRoperand(negate(bf.getRoperand()));
                System.out.println("DeMorganLaw");
                return bf;
            }
            return negate(f);
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should start with a NOT operator but starts with %s",
                        f.getOperator().getImage()
                )
        );
    }

    /** @return Returns true if, and only if, the formula needs the application of the distributive law
     * @param f the formula which needs the application of the distributive law,
     * it should have one of AND, OR as operator */
    public static boolean needsDistributiveLaw(BinaryFormula f){
        if(!(f.isOperator(AND) || f.isOperator(OR))) {
            throw new IllegalArgumentException(
                    String.format("The distribution rule can be only applied to formulas that have an AND/OR operator but the formula operator is: %s", f.getOperator())
            );
        }
        return (needsLeftDistributiveLaw(f) || needsRightDistributiveLaw(f));
    }

    /** @return Returns true if, and only if, the formula needs the application of the distributive law
     * on the left
     * @param f the formula which needs the application of the distributive law on left,
     * it should have one of AND, OR as operator and the right child should have as operator
     * the mirror one */
    public static boolean needsLeftDistributiveLaw(BinaryFormula f) {
        if(!(f.isOperator(AND) || f.isOperator(OR))) {
            throw new IllegalArgumentException(
                    String.format("The distribution rule can be only applied to formulas that have an AND/OR operator but the formula operator is: %s", f.getOperator())
            );
        }
        return f.getRoperand().isOperator(f.getOperator().getMirrorOperator());
    }

    /** @return Returns true if, and only if, the formula needs the application of the distributive law
     * on the right
     * @param f the formula which needs the application of the distributive law on the right,
     * it should have one of AND, OR as operator and the right child should have as operator
     * the mirror one */
    public static boolean needsRightDistributiveLaw(BinaryFormula f) {
        if(!(f.isOperator(AND) || f.isOperator(OR))) {
            throw new IllegalArgumentException(
                    String.format("The distribution rule can be only applied to formulas that have an AND/OR operator but the formula operator is: %s", f.getOperator())
            );
        }
        return f.getLoperand().isOperator(f.getOperator().getMirrorOperator());
    }


    public static BinaryFormula distributiveLaw(BinaryFormula f) {

        if(!(f.isOperator(AND) || f.isOperator(OR))) {
            throw new IllegalArgumentException(
                    String.format("The distribution rule can be only applied to formulas that have an AND/OR operator but the formula operator is: %s", f.getOperator())
            );
        }
        System.out.println("DistributiveLaw");
        if(f.getRoperand().isOperator(f.getOperator().getMirrorOperator())) return leftDistributiveLaw(f);
        if(f.getLoperand().isOperator(f.getOperator().getMirrorOperator())) return rightDistributiveLaw(f);
        throw new IllegalArgumentException(
                String.format("One of the two operand of the formula has to be a formula with the operator %s", f.getOperator().getMirrorOperator())
            );
    }

    /** A & (B | C) =>* (A & B) | (A & C) */
    private static BinaryFormula leftDistributiveLaw(BinaryFormula f){

        if(!f.getRoperand().isOperator(f.getOperator().getMirrorOperator())) {
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the right operand should be %s but is %s",
                            f.getOperator().getMirrorOperator(), f.getRoperand()
                    )
            );
        }
        BinaryFormula rc = (BinaryFormula) f.getRoperand();
        return new BinaryFormula(
                f.getOperator().getMirrorOperator(), // |
                // A & B
                new BinaryFormula(
                        f.getOperator(), // &
                        f.getLoperand().deepCopy(), // A
                        rc.getLoperand() // B
                ),
                // A & C
                new BinaryFormula(
                        f.getOperator(), // &
                        f.getLoperand().deepCopy(), // A
                        rc.getRoperand() // C
                )
        );
    }

    /** (A & B) | C =>* (A & C) | (B & C) */
    private static BinaryFormula rightDistributiveLaw(BinaryFormula f){

        if(!f.getLoperand().isOperator(f.getOperator().getMirrorOperator())){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the left operand should be %s but is %s",
                            f.getOperator().getMirrorOperator(), f.getLoperand()
                    )
            );
        }
        BinaryFormula lc = (BinaryFormula) f.getLoperand();
        // (A & C) | (B & C)
        return new BinaryFormula(
                f.getOperator().getMirrorOperator(), // |
                // A & C
                new BinaryFormula(
                        f.getOperator(), // &
                        lc.getLoperand(), // A
                        f.getRoperand().deepCopy() // C
                ),
                // B & C
                new BinaryFormula(
                        f.getOperator(), // &
                        lc.getRoperand(), // B
                        f.getRoperand().deepCopy() // C
                )
        );
    }










}
