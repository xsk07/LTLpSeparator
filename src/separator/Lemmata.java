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

        return new BinaryFormula (
                f1.getOperator(), // U
                f1.getLoperand(), // A
                // B&C
                new BinaryFormula(
                        op,  // &
                        f1.getRoperand(), // B
                        f2.getRoperand()  // C
                )
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
