package separator;

import formula.BinaryFormula;
import formula.Formula;
import formula.Operator;
import java.util.Arrays;
import static formula.Operator.*;

public abstract class Lemmas {

    /** LEMMA A.1
      * U(A,B) & U(C,D) =>* E = E1 | E2 | E3
      * E1 = U(A&C, B&D)
      * E2 = U(A & D & U(C,D), B&D)
      * E3 = U(C & B & U(A,B), B&D) */
    public static BinaryFormula lemmaA1(BinaryFormula f) {

        if(!(f.getOperator().equals(AND) | f.getOperator().equals(OR)) ) {
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula must be '&' or '|' but is '%s'",
                            f.getOperator()
                    )
            );
        }

        System.out.println("LemmaA1");

        BinaryFormula lc = (BinaryFormula) f.getLoperand();
        BinaryFormula rc = (BinaryFormula) f.getRoperand();
        Operator op = lc.getOperator();
        Formula[] fms = {
                lc.getLoperand(), // A
                lc.getRoperand(), // B
                rc.getLoperand(), // C
                rc.getRoperand()  // D
        };

        return (BinaryFormula) BinaryFormula.newDisjunction(Arrays.asList(
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

    /** @return Returns true, if and only if, the formula needs the application of the
      * reversed lemma A.2 */
    public static boolean needsReversedLemmaA2(BinaryFormula f) {

        if(!(f.isOperator(AND) || f.isOperator(OR))) {

            throw new IllegalArgumentException (
                    String.format(
                            "The operator of the formula should be '&' or '|' but is %s",
                            f.getOperator()
                    )
            );
        }
        return needsAndReversedLemmaA2(f) || needsOrReversedLemmaA2(f);
    }

    private static boolean needsAndReversedLemmaA2(BinaryFormula f) {
        BinaryFormula lc = (BinaryFormula) f.getLoperand();
        BinaryFormula rc = (BinaryFormula) f.getRoperand();
        boolean operatorIsAnd = f.getOperator().equals(AND);
        boolean sameLeftChild = lc.getLoperand().equalTo(rc.getLoperand());
        return operatorIsAnd && sameLeftChild;
    }

    private static boolean needsOrReversedLemmaA2(BinaryFormula f) {
        BinaryFormula lc = (BinaryFormula) f.getLoperand();
        BinaryFormula rc = (BinaryFormula) f.getRoperand();
        boolean operatorIsOr = f.getOperator().equals(OR);
        boolean sameRightChild = lc.getRoperand().equalTo(rc.getRoperand());
        return operatorIsOr && sameRightChild;
    }

    public static BinaryFormula reversedLemmaA2(BinaryFormula f) {

        if(!(f.isOperator(AND) || f.isOperator(OR))) {

            throw new IllegalArgumentException (
                    String.format(
                            "The operator of the formula should be '&' or '|' but is %s",
                            f.getOperator()
                    )
            );
        }

        BinaryFormula lc = (BinaryFormula) f.getLoperand();
        BinaryFormula rc = (BinaryFormula) f.getLoperand();

        boolean bothUntil = lc.isOperator(UNTIL) && rc.isOperator(UNTIL);
        boolean bothSince = lc.isOperator(SINCE) && rc.isOperator(SINCE);
        if(!(bothUntil || bothSince)) {
            throw new IllegalArgumentException (
                    "The operators of the two children should be both 'U' or 'S'"
            );
        }

        if(!(lc.getLoperand().equalTo(rc.getLoperand()))) {
            throw new IllegalArgumentException (
                    "The first operator of the two children had to be the same"
            );
        }

        Operator op = f.getOperator();

        if (op.equals(AND)) return andReversedLemmaA2(f);
        if(op.equals(OR))return orReversedLemmaA2(f);
        return f;



    }

    /** U(A,B) & U(A,C) =>* U(A, B&C) */
    private static BinaryFormula andReversedLemmaA2(BinaryFormula f) {
        BinaryFormula lc = (BinaryFormula) f.getLoperand(); // U(A,B)
        BinaryFormula rc = (BinaryFormula) f.getRoperand(); // U(A,C)
        Operator op = lc.getOperator(); // U
        // U(A, B&C)
        return new BinaryFormula (
                op, // U
                lc.getLoperand(), // A
                // B&C
                new BinaryFormula(
                        f.getOperator(),  // &
                        lc.getRoperand(), // B
                        rc.getRoperand()  // C
                )
        );
    }

    /** U(A,C) | U(B,C) =>* U(A|B, C) */
    private static BinaryFormula orReversedLemmaA2(BinaryFormula f) {
        BinaryFormula lc = (BinaryFormula) f.getLoperand(); // U(A,C)
        BinaryFormula rc = (BinaryFormula) f.getRoperand(); // U(B,C)
        Operator op = lc.getOperator(); // U
        // U(A|B, C)
        return new BinaryFormula (
                op, // U
                // A|B
                new BinaryFormula(
                        f.getOperator(),  // |
                        lc.getLoperand(), // A
                        rc.getLoperand()  // B
                ),
                lc.getRoperand() // C
        );
    }


}
