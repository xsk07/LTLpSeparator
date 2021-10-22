package converter;

import formula.*;
import static converter.FormulaConverter.convert;
import static formula.AtomConstant.*;
import static formula.Operator.*;

public abstract class ConversionRules {

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
    public static BinaryFormula ruleW(BinaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == UNLESS) {
            f.setOperator(UNTIL);
            return new BinaryFormula(
                    OR,
                    f,
                    convert(new UnaryFormula(
                            GLOB,
                            f.getLoperand().deepCopy(),
                            null
                    ))
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
                        f.getOperand().negate(),
                        new AtomicFormula(TRUE)
                )
        ).negate();

    }
}
