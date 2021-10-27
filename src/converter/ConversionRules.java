package converter;

import formula.*;
import static formula.AtomConstant.*;
import static formula.Operator.*;

public abstract class ConversionRules {

    /** Applies the equivalence rule of the Once operator.
     *  Rewriting rule: O q =>* q S true
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Once operator */
    public static Formula ruleO(UnaryFormula f) throws IllegalArgumentException {

        if(f.getOperator() == ONCE){
            return existentialRule(f, SINCE, TRUE);
        }

        throw new IllegalArgumentException(
                String.format(
                        "The formula must have O as operator but has %s",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Historically operator.
     *  Rewriting rule: H q =>* !(!q S true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Historically operator */
    public static Formula ruleH(UnaryFormula f) throws IllegalArgumentException {

        if(f.getOperator() == HIST) {
            return universalRule(f, SINCE);
        }

        throw new IllegalArgumentException(
                String.format(
                        "The formula must have H as operator but has %s",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Yesterday operator.
     *  Rewriting rule: Y q =>* q S false
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Yesterday operator */
    public static Formula ruleY(UnaryFormula f) throws IllegalArgumentException {

        if(f.getOperator() == YEST){
            return existentialRule(f, SINCE, FALSE);
        }

        throw new IllegalArgumentException(
                String.format(
                        "The formula must have Y as operator but has %s",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Finally operator.
     *  Rewriting rule: F q  =>* q U true
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Finally operator */
    public static Formula ruleF(UnaryFormula f) throws IllegalArgumentException {

        if(f.getOperator() == FIN) {
            return existentialRule(f, UNTIL, TRUE);
        }

        throw new IllegalArgumentException(
                String.format(
                        "The formula must have F as operator but has %s",
                        f.getOperator()
                )
        );
    }


    /** Applies the equivalence rule of the Next operator.
     *  Rewriting rule: X q =>* q U false
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Next operator */
    public static Formula ruleX(UnaryFormula f) throws IllegalArgumentException {

        if(f.getOperator() == NEXT){
            return existentialRule(f, UNTIL, FALSE);
        }

        throw new IllegalArgumentException(
                String.format(
                        "The formula must have X as operator but has %s",
                        f.getOperator()
                )
        );
    }


    /** Applies the equivalence rule of the Globally operator.
     *  Rewriting rule: G q =>* !(!q U true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Globally operator */
    public static Formula ruleG(UnaryFormula f) throws IllegalArgumentException {

        if(f.getOperator() == GLOB) {
            return universalRule(f, UNTIL);
        }

        throw new IllegalArgumentException(
                String.format(
                        "The formula must have G as operator but has %s",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Unless operator.
     *  Rewriting rule: p W q =>* (p U q) | G p
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Unless operator */
    public static BinaryFormula ruleW(BinaryFormula f) throws IllegalArgumentException {

        if(f.getOperator() == UNLESS) {
            return new BinaryFormula(
                    OR,
                    new BinaryFormula(
                            UNTIL,
                            f.getLoperand(),
                            f.getRoperand()
                    ),
                    ruleG( new UnaryFormula(
                            GLOB,
                            f.getLoperand().deepCopy(),
                            null)
                    )
            );

        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula must have W as operator but has %s",
                        f.getOperator()
                )
        );
    }


    /** Subroutine used by methods: ruleO, ruleY, ruleF, ruleX.
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
    /** Subroutine used by methods: ruleH, ruleG.
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
