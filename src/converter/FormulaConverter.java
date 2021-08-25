package converter;

import formula.*;
import static formula.Operator.*;
import static formula.AtomConstant.*;


public class FormulaConverter {

    /** Converts a formula, applying a set of equivalence and rewrite rules, into a form
     * containing only boolean and the two binary temporal operators since and until.
     * @param f The formula to be converted
     * @return Returns a formula containing only boolean and the binary operators since
     * and until */
    public static Formula convert(Formula f) throws IllegalArgumentException {
        if(f.isOperator()){
            OperatorFormula of = (OperatorFormula) f;
            if(of.isUnary()) {
                ((UnaryFormula) f).setOperand(convert(((UnaryFormula) f).getOperand()));
                return applyUnaryRule((UnaryFormula) f);
            }
            if(of.isBinary()){
                ((BinaryFormula) f).setLoperand(convert(((BinaryFormula) f).getLoperand()));
                ((BinaryFormula) f).setRoperand(convert(((BinaryFormula) f).getRoperand()));
                if(of.getOperator() == UNLESS) {
                    Formula opW = ruleW((BinaryFormula) f);
                    return convert(opW); /* for the conversion of the G operator in the
                    left branch of the formula */
                }
                return f;
            }
            return f;
        }
        return f;
    }


    /** Returns the formula which is the result of the application of
     * the unary rule corresponding to the operator of the formula got in input.
     * @return Returns the formula which is the result of the application of
     * the unary rule corresponding to the operator of the formula got in input
     * @param f An UnaryFormula */
    public static Formula applyUnaryRule(UnaryFormula f) throws IllegalArgumentException {
        return switch (f.getOperator()) {
            case ONCE -> ruleO(f);
            case HIST -> ruleH(f);
            case YEST -> ruleY(f);
            case FIN -> ruleF(f);
            case GLOB -> ruleG(f);
            case NEXT -> ruleX(f);
            default -> f;
        };
    }

    /** Applies the equivalence rule of the Once operator.
     * rewriting rule: O q =>* q S true
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Once operator */
    public static Formula ruleO(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == ONCE){
            return existentialRule(f, SINCE, TRUE);
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have O as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }

    /** Applies the equivalence rule of the Historically operator.
     * rewriting rule: H q =>* !(!q S true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Historically operator */
    public static Formula ruleH(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == HIST) {
            return universalRule(f, SINCE);
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have H as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }

    /** Applies the equivalence rule of the Yesterday operator.
     * rewriting rule: Y q =>* q S false
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Yesterday operator */
    public static Formula ruleY(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == YEST){
            return existentialRule(f, SINCE, FALSE);
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have Y as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }

    /** Applies the equivalence rule of the Finally operator.
     * rewriting rule: F q  =>* q U true
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Finally operator */
    public static Formula ruleF(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == FIN) {
            return existentialRule(f, UNTIL, TRUE);
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have F as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }


    /** Applies the equivalence rule of the Next operator.
     * rewriting rule: X q =>* q U false
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Next operator */
    public static Formula ruleX(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == NEXT){
            return existentialRule(f, UNTIL, FALSE);
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have X as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }


    /** Applies the equivalence rule of the Globally operator.
     * rewriting rule: G q =>* !(!q U true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Globally operator */
    public static Formula ruleG(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == GLOB){
            return universalRule(f, UNTIL);
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have G as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }

    /** Applies the equivalence rule of the Unless operator.
     * rewriting rule: p W q =>* (p U q) | G p
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Unless operator */
    public static Formula ruleW(BinaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == UNLESS) {
            f.setOperator(UNTIL);
            return new BinaryFormula(
                    OR,
                    f,
                    new UnaryFormula(
                            GLOB,
                            f.getLoperand().deepCopy()
                    )
            );

        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula should have W as operator but has %s",
                        f.getOperator().getImage()
                )
        );
    }


    /** A subroutine used by the methods: ruleO, ruleY, ruleF, ruleX.
     *  @see #ruleO(UnaryFormula)
     *  @see #ruleY(UnaryFormula)
     *  @see #ruleF(UnaryFormula)
     *  @see #ruleX(UnaryFormula) */
    private static Formula existentialRule(UnaryFormula f, Operator bOp, AtomConstant tVal) {

        return new BinaryFormula(
                bOp,
                f.getOperand(),
                new AtomicFormula(tVal)
        );

    }
    /** A subroutine used by the methods: ruleH, ruleG.
     *  @see #ruleH(UnaryFormula)
     *  @see #ruleG(UnaryFormula) */
    private static Formula universalRule(UnaryFormula f, Operator bOp) {

        return (
                new BinaryFormula(
                        bOp,
                        new UnaryFormula(NOT, f.getOperand()),
                        new AtomicFormula(TRUE)
                )
        ).negate();

    }

}
