package separator;

import formula.BinaryFormula;
import formula.Formula;
import formula.Operator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static formula.BinaryFormula.newDisjunction;
import static formula.Operator.*;


public abstract class Lemmata {

    /** Returns true if, and only if, f needs the application of the Lemma A2 */
    public static boolean needsLemmaA2(BinaryFormula f) {

        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula must be 'U' or 'S' but is '%s'",
                            f.getOperator()
                    )
            );
        }
        return (needsLemmaA2AND(f) || needsLemmaA2OR(f));
    }

    /** Returns true if, and only if, the formula needs the application of the Lemma A2 because of
     * the left operand that has the operator OR */
    public static boolean needsLemmaA2OR(BinaryFormula f) {
        return (
                (f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))
                        && f.getLoperand().isOperator(OR)
        );
    }

    /** Returns true if, and only if, the formula needs the application of the Lemma A2 because of
     * the right operand that has the operator AND */
    public static boolean needsLemmaA2AND(BinaryFormula f) {
        return (
                (f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))
                        && f.getRoperand().isOperator(AND)
        );
    }

    /** U((A|B), C) =>* U(A,C) | U(B,C) */
    public static BinaryFormula lemmaA2(BinaryFormula f) {

        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula must be 'U' or 'S' but is '%s'",
                            f.getOperator()
                    )
            );
        }

        // U((A|B), C) =>* U(A,C) | U(B,C)
        if(f.getLoperand().isOperator(OR)) return lemmaA2OR(f);

        // U(A, (B&C)) =>* U(A,B) & U(A,C)
        if(f.getRoperand().isOperator(AND)) return lemmaA2AND(f);

        else throw new IllegalArgumentException(
                String.format(
                        "The operator of the left child of the formula must be an '|' but is '%s' " +
                                "or the operator of the right child of the formula should be an AND but is %s ",
                        f.getLoperand().getImage(),
                        f.getRoperand().getImage()
                )
        );
    }

    public static BinaryFormula lemmaA2OR(BinaryFormula f) {
        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){

            throw new IllegalArgumentException (
                    String.format(
                            "The operator of the formula must be 'U' or 'S' but is '%s'",
                            f.getOperator()
                    )
            );
        }

        // U((A|B), C) =>* U(A,C) | U(B,C)
        if(f.getLoperand().isOperator(OR)) {
            BinaryFormula lf = (BinaryFormula) f.getLoperand();
            System.out.println("LemmaA2");
            // U(A,C) | U(B,C)
            return new BinaryFormula (
                    OR,
                    // U(A,C)
                    new BinaryFormula(
                            f.getOperator(), // U
                            lf.getLoperand(), // A
                            f.getRoperand().deepCopy() // C
                    ),
                    // U(B,C)
                    new BinaryFormula(
                            f.getOperator(), // U
                            lf.getRoperand(), // B
                            f.getRoperand().deepCopy() // C
                    )
            );
        }


        else throw new IllegalArgumentException (
                "The operator of the left child of the formula must be an '|'"
        );
    }

    public static BinaryFormula lemmaA2AND(BinaryFormula f){
        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){

            throw new IllegalArgumentException (
                    String.format(
                            "The operator of the formula should be S or U but is %s",
                            f.getOperator()
                    )
            );
        }


        // U(A, (B&C)) =>* U(A,B) & U(A,C)
        if(f.getRoperand().isOperator(AND)){
            BinaryFormula rf = (BinaryFormula) f.getRoperand();
            System.out.println("LemmaA2");
            // U(A,B) & U(A,C)
            return new BinaryFormula(
                    AND,
                    // U(A,B)
                    new BinaryFormula(
                            f.getOperator(), // U
                            f.getLoperand().deepCopy(), // A
                            rf.getLoperand() // B
                    ),
                    // U(A,C)
                    new BinaryFormula(
                            f.getOperator(), // U
                            f.getLoperand().deepCopy(), // A
                            rf.getRoperand() // C
                    )
            );
        }

        else throw new IllegalArgumentException (
                "The operator of the left child of the formula should be an AND"
        );
    }

    public static boolean needReversedLemmaA2(Operator op, BinaryFormula f1, BinaryFormula f2) {
        boolean sameOperator = f1.getOperator().equals(f2.getOperator());
        boolean childInCommon = false;
        if(op.equals(AND)) {
            childInCommon = f1.getLoperand().equalTo(f2.getLoperand());
        }
        if(op.equals(OR)) {
            childInCommon = f1.getRoperand().equalTo(f2.getRoperand());
        }
        return sameOperator && childInCommon;
    }


    /** AND case: U(A,B) & U(A,C) =>* U(A, B&C)
     *  OR case:  U(A,C) | U(B,C) =>* U(A|B, C) */
    public static BinaryFormula reversedLemmaA2(Operator op, BinaryFormula f1, BinaryFormula f2) {
        System.out.println("ReversedLemmaA2");
        if(op.equals(AND)) return reversedLemmaA2AND(f1, f2);
        if(op.equals(OR)) return reversedLemmaA2OR(f1, f2);
        throw new IllegalArgumentException(
                String.format(
                        "The operator of the formula must be '&' or '|' but is '%s'",
                        op
                )
        );
    }

    /**  U(A,B) & U(A,C) =>* U(A, B&C) */
    private static BinaryFormula reversedLemmaA2AND(BinaryFormula f1, BinaryFormula f2) {
        return new BinaryFormula (
                f1.getOperator(), // U
                f1.getLoperand(), // A
                // B&C
                new BinaryFormula(
                        AND,  // &
                        f1.getRoperand(), // B
                        f2.getRoperand()  // C
                )
        );
    }

    /**  U(A,C) | U(B,C) =>* U(A|B, C) */
    private static BinaryFormula reversedLemmaA2OR(BinaryFormula f1, BinaryFormula f2) {
        return new BinaryFormula (
                f1.getOperator(), // U
                // A | B
                new BinaryFormula(
                        OR,  // &
                        f1.getLoperand(), // A
                        f2.getLoperand()  // B
                ),
                f1.getRoperand() // C
        );
    }

    // pre: fms obtained by lemma A1
    public static BinaryFormula reversedLemmaA2(Collection<BinaryFormula> fms) {

        System.out.println("ReversedLemmaA2");

        BinaryFormula f = fms.iterator().next();
        ArrayList<Formula> leftChildren = new ArrayList<>();
        fms.forEach(z -> leftChildren.add(z.getLoperand()));
        BinaryFormula lc = (BinaryFormula) newDisjunction(leftChildren);
        return new BinaryFormula(f.getOperator(), lc, f.getRoperand());
    }


    /** LEMMA A.1
     * U(A,B) & U(C,D) =>* E = E1 | E2 | E3
     * E1 = U(A&C, B&D)
     * E2 = U(A & D & U(C,D), B&D)
     * E3 = U(C & B & U(A,B), B&D) */
    public static BinaryFormula lemmaA1(BinaryFormula f1, BinaryFormula f2) {
        Operator op_f1 = f1.getOperator();
        Operator op_f2 = f2.getOperator();

        if(!((op_f1.equals(UNTIL) || op_f1.equals(SINCE)) && op_f1.equals(op_f2))) {
            throw new IllegalArgumentException(
                    String.format(
                            "The operators of the two formulae must have the same operand but the f1 has %s and f2 has %s as operand",
                            op_f1, op_f2
                    )
            );
        }

        System.out.println("LemmaA1");

        Operator op = f1.getOperator();
        Formula[] fms = {
                f1.getLoperand(), // A
                f1.getRoperand(), // B
                f2.getLoperand(), // C
                f2.getRoperand()  // D
        };

        return (BinaryFormula) newDisjunction(Arrays.asList(
                        lemmaA1_E1(fms, op), // U(A&C, B&D)
                        lemmaA1_E2(fms, op), // U(A & D & U(C,D), B&D)
                        lemmaA1_E3(fms, op)  // U(C & B & U(A,B), B&D)
                )
        );
    }

    /** E1 = U(A&C, B&D) */
    private static BinaryFormula lemmaA1_E1(Formula[] fms, Operator op) {
        /// U(A&C, B&D)
        return new BinaryFormula(
                op,  // U
                // A&C
                new BinaryFormula(
                        AND, // &
                        fms[0].deepCopy(), // A
                        fms[2].deepCopy()  // C
                ),
                // B&D
                new BinaryFormula(
                        AND, // &
                        fms[1].deepCopy(), // B
                        fms[3].deepCopy()  // D
                )
        );
    }

    /** E2 = U(A & D & U(C,D), B&D) */
    private static BinaryFormula lemmaA1_E2(Formula[] fms, Operator op) {
        // U(A & D & U(C,D), B&D)
        return new BinaryFormula(
                op, // U
                // A & D & U(C,D)
                BinaryFormula.newConjunction(Arrays.asList(
                                fms[0].deepCopy(), // A
                                fms[3].deepCopy(), // D
                                // U(C,D)
                                new BinaryFormula(
                                        op, // U
                                        fms[2].deepCopy(), // C
                                        fms[3].deepCopy()  // D
                                )
                        )
                ),
                // B&D
                new BinaryFormula(
                        AND, // &
                        fms[1].deepCopy(), // B
                        fms[3].deepCopy()  // D
                )
        );
    }

    /** E3 = U(C & B & U(A,B), B&D) */
    private static BinaryFormula lemmaA1_E3(Formula[] fms, Operator op) {
        // U(C & B & U(A,B), B&D)
        return new BinaryFormula(
                op, // U
                // U(C & B & U(A,B)
                BinaryFormula.newConjunction(Arrays.asList(
                                fms[2].deepCopy(), // C
                                fms[1].deepCopy(), // B
                                // U(A,B)
                                new BinaryFormula(
                                        op, // U
                                        fms[0].deepCopy(), // A
                                        fms[1].deepCopy()  // B
                                )
                        )
                ),
                // B&D
                new BinaryFormula(
                        AND, // &
                        fms[1].deepCopy(), // B
                        fms[3].deepCopy()  // D
                )
        );
    }

}
