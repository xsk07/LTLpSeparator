package separator.eliminator;

import formula.*;
import java.util.Arrays;
import static formula.Operator.*;
import converter.FormulaConverter;
import static formula.AtomConstant.FALSE;
import static formula.BinaryFormula.newConjunction;
import static formula.BinaryFormula.newDisjunction;
import static separator.eliminator.SubformulaePatterns.*;

public abstract class EliminationRules {

    private static final FormulaConverter cnv = new FormulaConverter();

    /** ELIMINATION 1
     *  S(a & U(A,B), q) =>* E = E1 | E2 | E3, where:
     *  E1 = S(a,q) & S(a,B) & B & U(A,B)
     *  E2 = A & S(a, B & q)
     *  E3 = S(A & q & S(a,B) & S(a,q), q)
     *  @param fms The subformulas needed for the elimination
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination1(Formula[] fms, Operator op) {
        System.out.println("Elimination1");
        return (BinaryFormula) BinaryFormula.newDisjunction(Arrays.asList(
                        e1_E1(fms, op), // S(a,q) & S(a,B) & B & U(A,B)
                        e1_E2(fms, op), // A & S(a, B & q)
                        e1_E3(fms, op)  // S(A & q & S(a,B) & S(a,q), q)
                )
        );
    }

    /** E1 = S(a,q) & S(a,B) & B & U(A,B) */
    private static BinaryFormula e1_E1(Formula[] fms, Operator op) {
        // S(a,q) & S(a,B) & B & U(A,B)
        return (BinaryFormula) newConjunction(Arrays.asList(
                        subformulaPattern1(fms, op), // S(a,q)
                        subformulaPattern2(fms, op), // S(a,B)
                        fms[2].deepCopy(), // B
                        subformulaPattern3(fms, op.getMirrorOperator())  // U(A,B)
                )
        );
    }

    /** E2 = A & S(a,B & q) */
    private static BinaryFormula e1_E2(Formula[] fms, Operator op) {
        // A & S(a, B & q)
        return new BinaryFormula(
                AND, // &
                fms[1].deepCopy(), // A
                subformulaPattern14(fms, op) // S(a, q & B)
        );
    }

    /** E3 = S(A & q & S(a,B) & S(a,q), q) */
    private static BinaryFormula e1_E3(Formula[] fms, Operator op) {
        //S(A & q & S(a,B) & S(a,q), q)
        return new BinaryFormula(
                op, // S
                // A & q & S(a,B) & S(a,q)
                newConjunction(Arrays.asList(
                                fms[1].deepCopy(), // A
                                fms[3].deepCopy(), // q
                                subformulaPattern2(fms, op), // S(a,B)
                                subformulaPattern1(fms, op) // S(a,q)
                        )
                ),
                fms[3].deepCopy() // q
        );
    }


    /** ELIMINATION 2
     *  S(a, q | U(A,B)) =>* E = E1 | E2 | E3, where:
     *  E1 = S(a, false)
     *  E2 = (A | (B & U(A,B))) & S(a, !a & !c)
     *  E3 = !(A | (B & U(A,B))) & S(a, !a & !c) & S(!q & !a, !a & !A)
     *  c = S(!q & !a, !a & !A) & !A & !B
     *  @param fms The subformulas needed for the elimination.
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination2(Formula[] fms, Operator op) {
        System.out.println("Elimination2");
        return (BinaryFormula) newDisjunction(Arrays.asList(
                        e2_E1(fms, op), // S(a, false)
                        e2_E2(fms, op), // (A | (B & U(A,B))) & S(a, !a & !c)
                        e2_E3(fms, op)  // !(A | (B & U(A,B))) & S(a, !a & !c) & S(!q & !a, !a & !A)
                )
        );
    }

    /** E1 = S(a, false)
     * @return Returns the 1st formula of the disjunction of the Elimination 3 */
    private static BinaryFormula e2_E1(Formula[] fms, Operator op) {
        // S(a, false)
        return new BinaryFormula(
                op, // S
                fms[0].deepCopy(), // a
                new AtomicFormula(FALSE) // false
        );
    }

    /** E2 = (A | (B & U(A,B))) & S(a, !a & !c)
     *  @return Returns the 2nd formula of the disjunction of the Elimination 3 */
    private static BinaryFormula e2_E2(Formula[] fms, Operator op) {
        // (A | (B & U(A,B))) & S(a, !a & !c)
        return new BinaryFormula(
                AND, // &
                subformulaPattern5(fms, op.getMirrorOperator()), // A | (B & U(A,B))
                subformulaPattern6(fms, op)  // S(a, !a & !c)
        );
    }

    /** E3 = !(A | (B & U(A,B))) & S(a, !a & !c) & S(!q & !a, !a & !A)
     *  @return Returns the 3rd formula of the disjunction of the Elimination 3 */
    private static BinaryFormula e2_E3(Formula[] fms, Operator op) {
        // !(A | (B & U(A,B))) & S(a, !a & !c) & S(!q & !a, !a & !A)
        return (BinaryFormula) newConjunction(Arrays.asList(
                        subformulaPattern5(fms, op.getMirrorOperator()).negate(), // !(A | (B & U(A,B)))
                        subformulaPattern6(fms, op), // S(a, !a & !c)
                        subformulaPattern7(fms, op) // S(!q & !a, !a & !A)
                )
        );
    }


    /** ELIMINATION 3
     *  S(a & !U(A,B), q) =>* E = E1 | E2 | E3, where:
     *  E1 = S(a, q & !A) & !A & !U(A,B)
     *  E2 = !A & !B & S(a, !A & q)
     *  E3 = S(!A & !B & q & S(a, !A & q), q)
     *  @param fms The subformulas needed for the elimination.
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination3(Formula[] fms, Operator op) {
        System.out.println("Elimination3");
        return (BinaryFormula) newDisjunction(Arrays.asList(
                        e3_E1(fms, op), // S(a, q & !A) & !A & !U(A,B)
                        e3_E2(fms, op), // !A & !B & S(a, !A & q)
                        e3_E3(fms, op)  // S(!A & !B & q & S(a, !A & q), q)
                )
        );
    }

    /** E1 = S(a, q & !A) & !A & !U(A,B) */
    private static BinaryFormula e3_E1(Formula[] fms, Operator op) {
        // S(a, q & !A) & !A & !U(A,B)
        return (BinaryFormula) newConjunction(Arrays.asList(
                        subformulaPattern8(fms, op), // S(a, q & !A)
                        fms[1].deepCopy().negate(), // !A
                        subformulaPattern3(fms, op.getMirrorOperator()).negate() // !U(A,B)
                )
        );
    }

    /** E2 = !A & !B & S(a, !A & q) */
    private static BinaryFormula e3_E2(Formula[] fms, Operator op) {
        // !A & !B & S(a, !A & q)
        return new BinaryFormula(
                AND, // &
                subformulaPattern9(fms), // !A & !B
                subformulaPattern8(fms, op) // S(a, !A & q)
        );
    }

    /** E3 = S(!A & !B & q & S(a, !A & q), q) */
    private static BinaryFormula e3_E3(Formula[] fms, Operator op) {
        // S(!A & !B & q & S(a, !A & q), q)
        return new BinaryFormula(
                op, // S
                // !A & !B & q & S(a, !A & q)
                newConjunction(Arrays.asList(
                                subformulaPattern9(fms), // !A & !B
                                fms[3].deepCopy(), // q
                                subformulaPattern8(fms, op) // S(a, !A & q)
                        )
                ),
                fms[3].deepCopy() // q
        );
    }


    /** ELIMINATION 4
     *  S(a, q | !U(A,B)) =>* !S(!q & U(A,B) & !a, !a) & Oa
     *  @param fms The subformulas needed for the elimination.
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination4(Formula[] fms, Operator op) {
        System.out.println("Elimination4");
        Operator uOp = ONCE;
        if(op.equals(UNTIL))  uOp = FIN;
        // !S(!q & U(A,B) & !a, !a) & Oa
        return new BinaryFormula(
                AND,
                // !S(!q & U(A,B) & !a, !a)
                new BinaryFormula(
                        op, // S
                        subformulaPattern17(fms, op.getMirrorOperator()), // !q & U(A,B) & !a
                        fms[0].deepCopy().negate() // !a
                ).negate(),
                // Oa
                cnv.convert(
                        new UnaryFormula(
                                uOp, // O
                                fms[0].deepCopy() // a
                        )
                )
        );
    }


    /** ELIMINATION 5
     *  S(a & U(A,B), q | U(A,B)) =>* E = E1 | E2 | E3, where:
     *  E1 = S(a,B) & (A | (B & U(A,B)))
     *  E2 = S(A & S(a,B), !b0) & d
     *  E3 = S(A & S(a,B), !b0) & !d & !S(!q, !A)
     *  and where:
     *  b0 = !A & !B & S(!q, !A)
     *  d = A | (B & U(A,B))
     *  @param fms The subformulas needed for the elimination.
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination5(Formula[] fms, Operator op){
        System.out.println("Elimination5");
        return (BinaryFormula) newDisjunction(Arrays.asList(
                        e5_E1(fms, op), // S(a,B) & (A | (B & U(A,B)))
                        e5_E2(fms, op), // S(A & S(a,B), !b0) & d
                        e5_E3(fms, op)  // S(A & S(a,B), !b0) & !d & !S(!q, !A)
                )
        );

    }

    /** E1 = S(a,B) & (A | (B & U(A,B))) */
    private static BinaryFormula e5_E1(Formula[] fms, Operator op){
        // // S(a,B) & (A | (B & U(A,B)))
        return new BinaryFormula(
                AND, // &
                subformulaPattern2(fms, op), // S(a,B)
                subformulaPattern5(fms, op.getMirrorOperator())  // A | (B & U(A,B))
        );
    }

    /** E2 = S(A & S(a,B), !b0) & d */
    private static BinaryFormula e5_E2(Formula[] fms, Operator op){
        // S(A & S(a,B), !b0) & d
        return new BinaryFormula(
                AND, // &
                subformulaPattern10(fms, op), // S(A & S(a,B), !b0)
                new_d(fms, op.getMirrorOperator()) // d = A | (B & U(A,B))
        );
    }

    /** E3 = S(A & S(a,B), !b0) & !d & !S(!q, !A) */
    private static BinaryFormula e5_E3(Formula[] fms, Operator op){
        // S(A & S(a,B), !b0) & !d & !S(!q, !A)
        return (BinaryFormula) newConjunction(Arrays.asList(
                        subformulaPattern10(fms, op), // S(A & S(a,B), !b0)
                        new_d(fms, op.getMirrorOperator()).negate(), // !d
                        subformulaPattern11(fms, op).negate() // !S(!q, !A)
                )
        );
    }


    /** ELIMINATION 6
     *  S(a & !U(A,B), q | U(A,B)) =>* E = E1 | E2 | E3, where:
     *  E1 = S(a, !A & q) & !A & !B
     *  E2 = S(!A & !B & (q | U(A,B)) & S(a, !A & q), q | U(A,B))
     *  E3 = S(a, q & !A) & !A & !U(A,B)
     *  @param fms The subformulas needed for the elimination.
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination6(Formula[] fms, Operator op) {
        System.out.println("Elimination6");
        return (BinaryFormula) newDisjunction(Arrays.asList(
                        e6_E1(fms, op), // S(a, !A & q) & !A & !B
                        e6_E2(fms, op), // S(!A & !B & (q | U(A,B)) & S(a, !A & q), q | U(A,B))
                        e6_E3(fms, op) // S(a, q & !A) & !A & !U(A,B)
                )
        );
    }

    /** E1 = S(a, !A & q) & !A & !B */
    private static BinaryFormula e6_E1(Formula[] fms, Operator op) {
        return new BinaryFormula(
                AND, // &
                subformulaPattern9(fms), // !A & !B
                subformulaPattern8(fms, op) //  S(a, !A & q)
        );
    }

    /** E2 = S(!A & !B & (q | U(A,B)) & S(a, !A & q), q | U(A,B)) */
    private static BinaryFormula e6_E2(Formula[] fms, Operator op){
        // S(!A & !B & (q | U(A,B)) & S(a, !A & q), q | U(A,B))
        return new BinaryFormula(
                op, // S
                // !A & !B & (q | U(A,B)) & S(a, !A & q)
                newConjunction(Arrays.asList(
                                subformulaPattern19(fms, op), // !A & !B & S(a, !A & q)
                                subformulaPattern12(fms, op.getMirrorOperator())  // q | U(A,B)
                        )
                ),
                subformulaPattern12(fms, op.getMirrorOperator()) // q | U(A,B)
        );
    }


    /** E3 = (a S (q & !A)) & !A & !(AUB) */
    private static BinaryFormula e6_E3(Formula[] fms, Operator op){

        // ((aS(q&!A)) & !A) & !(AUB)
        return new BinaryFormula(
                AND,
                // (aS(q&!A)) & !A
                new BinaryFormula(
                        AND,
                        subformulaPattern8(fms, op), // a S (q&!A)
                        fms[1].deepCopy().negate() // !A
                ),
                subformulaPattern3(fms, op.getMirrorOperator()).negate() // !(AUB)
        );

    }


    /** ELIMINATION 7
     *  S(a & U(A,B), q | !U(A,B)) =>* E = E1 | E2 | E3, where:
     *  E1 = S(A & (q | !U(A,B)) & S(a, B & q), q | !U(A,B))
     *  E2 = S(a, B & q) & A
     *  E3 = S(a, B & q) & B & U(A,B)
     *  @param fms The subformulas needed for the elimination.
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination7(Formula[] fms, Operator op) {
        System.out.println("Elimination7");
        return (BinaryFormula) newDisjunction(Arrays.asList(
                        e7_E1(fms, op), // S(A & (q | !U(A,B)) & S(a, B & q), q | !(AUB))
                        e7_E2(fms, op), // S(a, B & q) & A
                        e7_E3(fms, op)  // S(a, B & q) & B & U(A,B)
                )
        );
    }

    /** E1 = S(A & (q | !U(A,B)) & S(a, B & q), q | !(AUB)) */
    private static BinaryFormula e7_E1(Formula[] fms, Operator op) {
        // S(A & (q | !U(A,B)) & S(a, B & q), q | !(AUB))
        return new BinaryFormula(
                op, // S
                // A & (q | !U(A,B)) & S(a, B & q)
                newConjunction(Arrays.asList(
                                fms[1].deepCopy(), // A
                                subformulaPattern13(fms, op.getMirrorOperator()), // q | !U(A,B)
                                subformulaPattern14(fms, op)  // S(a, B & q)
                        )
                ),
                subformulaPattern13(fms, op.getMirrorOperator()) // q | !U(A,B)
        );
    }

    /** E2 = S(a, B & q) & A */
    private static BinaryFormula e7_E2(Formula[] fms, Operator op){
        // S(a, B & q) & A
        return new BinaryFormula(
                AND, // &
                subformulaPattern14(fms, op), // S(a, B & q)
                fms[1].deepCopy() // A
        );
    }

    /** E3 = S(a, B & q) & B & U(A,B) */
    private static BinaryFormula e7_E3(Formula[] fms, Operator op){
        // S(a, B & q) & B & U(A,B)
        return (BinaryFormula) newConjunction(Arrays.asList(
                        subformulaPattern14(fms, op), // S(a, B & q)
                        fms[2].deepCopy(), // B
                        subformulaPattern3(fms, op.getMirrorOperator()) // U(A,B)
                )
        );
    }

    /** ELIMINATION 8
     *  S(a & !U(A,B), q | !U(A,B)) =>* E = !(E1 | E2 | E3), where:
     *  E1 = H(!a | U(A,B))
     *  E2 = S(!q & U(A,B) & !a, !a | U(A,B))
     *  E3 = S(!q & U(A,B), !a | U(A,B))
     *  @param fms The subformulas needed for the elimination.
     *  Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */

    public static OperatorFormula elimination8(Formula[] fms, Operator op) {
        System.out.println("Elimination8");
        return newDisjunction(Arrays.asList(
                        cnv.convert(e8_E1(fms, op)), // H(!a | U(A,B))
                        e8_E2(fms, op), // S(!q & U(A,B) & !a, !a | U(A,B))
                        e8_E3(fms, op) // S(!q & U(A,B), !a | U(A,B))
                )
        ).negate();
    }

    /** E1 = H(!a | U(A,B)) */
    private static UnaryFormula e8_E1(Formula[] fms, Operator op) {
        Operator uOp = HIST;
        if(op.equals(UNTIL)) uOp = GLOB;
        // H(!a | U(A,B))
        return new UnaryFormula(
                uOp, // H
                subformulaPattern15(fms, op.getMirrorOperator()) // !a | U(A,B)
        );
    }

    /** E2 = S(!q & U(A,B) & !a, !a | U(A,B)) */
    private static BinaryFormula e8_E2(Formula[] fms, Operator op) {
        // S(!q & U(A,B) & !a, !a | U(A,B))
        return new BinaryFormula(
                op, // S
                // !q & U(A,B) & !a
                new BinaryFormula(
                        AND, // &
                        subformulaPattern16(fms, op.getMirrorOperator()), // !q & U(A,B)
                        fms[0].deepCopy().negate() // !a
                ),
                subformulaPattern15(fms, op.getMirrorOperator()) // !a | U(A,B)
        );
    }

    /** E3 = S(!q & U(A,B), !a | U(A,B))  */
    private static BinaryFormula e8_E3(Formula[] fms, Operator op) {
        // S(!q & U(A,B), !a | U(A,B))
        return new BinaryFormula(
                op, // S
                subformulaPattern16(fms, op.getMirrorOperator()), // !q & U(A,B)
                subformulaPattern15(fms, op.getMirrorOperator())  // !a | U(A,B)
        );
    }

}
