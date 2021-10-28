package converter;

import formula.*;
import static formula.AtomConstant.*;
import static formula.Operator.*;

public abstract class ConversionRules {

    /** Applies the equivalence rule of the Once operator.
     *  Rewriting rule: Oq =>* S(q, true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Once operator */
    public static Formula ruleO(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == ONCE) return existentialRule(SINCE, f, TRUE);
        throw new IllegalArgumentException (
                String.format(
                        "The formula must have 'O' as operator but has '%s'",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Historically operator.
     *  Rewriting rule: Hq =>* !S(!q, true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Historically operator */
    public static Formula ruleH(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == HIST) return universalRule(SINCE, f);
        throw new IllegalArgumentException(
                String.format(
                        "The formula must have 'H' as operator but has '%s'",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Yesterday operator.
     *  Rewriting rule: Yq =>* S(q, false)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Yesterday operator */
    public static Formula ruleY(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == YEST) return existentialRule(SINCE, f, FALSE);
        throw new IllegalArgumentException(
                String.format(
                        "The formula must have 'Y' as operator but has '%s'",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Finally operator.
     *  Rewriting rule: Fq  =>* U(q, true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Finally operator */
    public static Formula ruleF(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == FIN) return existentialRule(UNTIL, f, TRUE);
        throw new IllegalArgumentException(
                String.format(
                        "The formula must have 'F' as operator but has '%s'",
                        f.getOperator()
                )
        );
    }


    /** Applies the equivalence rule of the Next operator.
     *  Rewriting rule: Xq =>* U(q, false)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Next operator */
    public static Formula ruleX(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == NEXT) return existentialRule(UNTIL, f, FALSE);
        throw new IllegalArgumentException(
                String.format(
                        "The formula must have 'X' as operator but has '%s'",
                        f.getOperator()
                )
        );
    }


    /** Applies the equivalence rule of the Globally operator.
     *  Rewriting rule: Gq =>* !U(!q, true)
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Globally operator */
    public static Formula ruleG(UnaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == GLOB) return universalRule(UNTIL, f);
        throw new IllegalArgumentException(
                String.format(
                        "The formula must have 'G' as operator but has '%s'",
                        f.getOperator()
                )
        );
    }

    /** Applies the equivalence rule of the Unless operator.
     *  Rewriting rule: W(p,q) =>* U(p,q) | Gq
     *  @param f An UnaryFormula
     *  @return An equivalent formula without the Unless operator */
    public static BinaryFormula ruleW(BinaryFormula f) throws IllegalArgumentException {
        if(f.getOperator() == UNLESS) {
            return new BinaryFormula(
                    OR, // |
                    new BinaryFormula(
                            UNTIL, // U
                            f.getLoperand(), // p
                            f.getRoperand()  // q
                    ),
                    ruleG(new UnaryFormula(
                            GLOB, // G
                            f.getRoperand().deepCopy() // q
                        )
                    )
            );
        }
        throw new IllegalArgumentException(
                String.format(
                        "The formula must have 'W' as operator but has '%s'",
                        f.getOperator()
                )
        );
    }


    /** Subroutine used by methods: ruleO, ruleY, ruleF, ruleX.
     *  @see #ruleO(UnaryFormula)
     *  @see #ruleY(UnaryFormula)
     *  @see #ruleF(UnaryFormula)
     *  @see #ruleX(UnaryFormula) */
    private static Formula existentialRule(Operator bOp, UnaryFormula f, AtomConstant tVal) {
        return new BinaryFormula(
                bOp,
                f.getOperand(),
                new AtomicFormula(tVal)
        );

    }
    /** Subroutine used by methods: ruleH, ruleG.
     *  @see #ruleH(UnaryFormula)
     *  @see #ruleG(UnaryFormula) */
    private static Formula universalRule(Operator bOp, UnaryFormula f) {
        return (new BinaryFormula(
                        bOp,
                        f.getOperand().negate(),
                        new AtomicFormula(TRUE)
                )
            ).negate();
    }

}
