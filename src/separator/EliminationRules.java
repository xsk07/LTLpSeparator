package separator;

import formula.*;
import java.util.ArrayList;
import static converter.FormulaConverter.convert;
import static formula.AtomConstant.*;
import static formula.Operator.*;
import static separator.SubformulasPatterns.*;

public abstract class EliminationRules {

    /** ELIMINATION 1
     * (a&(AUB))Sq =>* E = E1 | E2 | E3, where:
     * E1 = aSq & aSB & B & AUB
     * E2 = A & aS(B&q)
     * E3 = (A & q & aSB & aSq )Sq
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination1(ArrayList<Formula> fms, Operator op) {

        return ternaryDisjunction(
                e1_E1(fms, op), // (((a S q) & (a S B)) & B) & (AUB)
                e1_E2(fms, op), // A&(aS(B&q))
                e1_E3(fms, op) // ((A&q & aSB & aSq) S q)
        );

    }


    /** E1 = (((a S q) & (a S B)) & B) & (AUB) */
    private static BinaryFormula e1_E1(ArrayList<Formula> fms, Operator op){

        // (aSq) & (aSB)
        BinaryFormula and3_E1 = new BinaryFormula(
                AND,
                subformulaPattern1(fms, op), // aSq
                subformulaPattern2(fms, op) // aSB
        );

        // ((aSq) & (aSB)) & B
        BinaryFormula and2_E1 = new BinaryFormula(
                AND,
                and3_E1, // (aSq) & (aSB)
                fms.get(2).deepCopy() // B
        );

        // (((a S q) & (a S B)) & B) & (AUB)
        return new BinaryFormula(
                AND,
                and2_E1, // ((aSq) & (aSB)) & B
                subformulaPattern3(fms, op) // AUB
        );

    }

    /** E2 = A&(aS(q&B)) */
    public static BinaryFormula e1_E2(ArrayList<Formula> fms, Operator op){

        return new BinaryFormula(
                AND,
                fms.get(1).deepCopy(), // A
                subformulaPattern14(fms, op) // aS(q&B)
        );

    }

    /** E3 = (A & q & aSB & aSq )Sq */
    public static BinaryFormula e1_E3(ArrayList<Formula> fms, Operator op){

        // A&q
        BinaryFormula and1_E3 = new BinaryFormula(
                AND,
                fms.get(1).deepCopy(), // A
                fms.get(3).deepCopy() // q
        );


        // A&q & aSB
        BinaryFormula and2_E3 = new BinaryFormula(
                AND,
                and1_E3, // A&q
                subformulaPattern2(fms, op) // aSB
        );

        // A&q & aSB & aSq
        BinaryFormula and3_E3 = new BinaryFormula(
                AND,
                and2_E3, // A&q & aSB
                subformulaPattern1(fms, op) // aSq
        );

        // (A&q & aSB & aSq) S q
        return new BinaryFormula(
                op, // S
                and3_E3, // A&q & aSB & aSq
                fms.get(3).deepCopy() // q
        );
    }

    /** ELIMINATION 2
     * (a&!(AUB))Sq =>* E = E1 | E2 | E3, where:
     * E1 = (a S (q & !A)) & !A & !(AUB)
     * E2 = !A & !B & (aS(!A&q))
     * E3 = (!A & !B & q & (aS(!A&q))) S q
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination2(ArrayList<Formula> fms, Operator op) {

        return ternaryDisjunction(
                e2_E1(fms, op), // (a S (q & !A)) & !A & !(AUB)
                e2_E2(fms, op), // !A & !B & (aS(!A&q))
                e2_E3(fms, op) // (!A & !B & q & (aS(!A&q))) S q
        );

    }

    /** E1 = (a S (q & !A)) & !A & !(AUB) */
    public static BinaryFormula e2_E1(ArrayList<Formula> fms, Operator op){

        // ((a S (q & !A)) & !A) & !(AUB)
        return new BinaryFormula(
                AND,
                // (a S (q & !A)) & !A
                new BinaryFormula(
                        AND,
                        subformulaPattern8(fms, op), // a S (q & !A)
                        fms.get(1).deepCopy().negate() // !A
                ),
                subformulaPattern3(fms, op).negate() // !(AUB)
        );

    }

    /** E2 = !A & !B & (aS(!A&q)) */
    public static BinaryFormula e2_E2(ArrayList<Formula> fms, Operator op){

        // (!A & !B) & (a S (q & !A))
        return new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                subformulaPattern8(fms, op) // a S (q & !A)
        );

    }

    /** E3 = (!A & !B & q & (aS(!A&q))) S q */
    public static BinaryFormula e2_E3(ArrayList<Formula> fms, Operator op){

        // (!A & !B) & q
        BinaryFormula and2 = new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                fms.get(3).deepCopy() // q
        );

        // ((!A & !B) & q) & (aS(!A&q))
        BinaryFormula and3 = new BinaryFormula(
                AND,
                and2, // (!A & !B) & q
                subformulaPattern8(fms, op) // aS(!A&q)
        );

        // (((!A & !B) & q) & (aS(!A&q))) S q
        return new BinaryFormula(
                op, // S
                and3, // ((!A & !B) & q) & (aS(!A&q))
                fms.get(3).deepCopy() // q
        );

    }


    /** ELIMINATION 3
     * aS(q|(AUB)) =>* E = E1 | E2 | E3, where:
     * E1 = a S false
     * E2 = (A | (B & (AUB))) & (a S (!a & !c))
     * E3 = !(A | (B & (AUB))) & (a S (!a & !c)) & ((!q & !a)S(!a & !A))
     * c = ((!q & !a)S(!a & !A) & !A & !B)
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination3(ArrayList<Formula> fms, Operator op) {

        return ternaryDisjunction(
                e3_E1(fms, op), // a S false
                e3_E2(fms, op), // (A | (B & (AUB))) & (a S (!a & !c))
                e3_E3(fms, op) // !(A | (B & (AUB))) & (a S (!a & !c)) & ((!q & !a)S(!a & !A))
        );

    }

    /** E1 = a S false */
    private static BinaryFormula e3_E1(ArrayList<Formula> fms, Operator op){

        return new BinaryFormula(
                op, // S
                fms.get(0).deepCopy(), // a
                new AtomicFormula(FALSE) // false
        );
    }

    /** E2 = (A | (B & (AUB))) & (a S (!a & !c))
     * @return Returns the 2nd formula of the disjunction of the Elimination 2 */
    private static BinaryFormula e3_E2(ArrayList<Formula> fms, Operator op){

        return new BinaryFormula(
                AND,
                subformulaPattern5(fms, op), // A | (B & (AUB))
                subformulaPattern6(fms, op) // a S (!a & !c)
        );

    }

    /** E3 = !(A | (B & (AUB))) & (a S (!a & !c)) & ((!q & !a)S(!a & !A))
     * @return Returns the 3nd formula of the disjunction of the Elimination 2 */
    private static BinaryFormula e3_E3(ArrayList<Formula> fms, Operator op){

        return new BinaryFormula(
                AND,
                // !(A | (B & (AUB))) & (a S (!a & !c))
                new BinaryFormula(
                        AND,
                        subformulaPattern5(fms, op).negate(), // !(A | (B & (AUB)))
                        subformulaPattern6(fms, op) // a S (!a & !c)
                ),
                subformulaPattern7(fms, op) // (!q & !a) S (!a & !A)
        );

    }

    /** ELIMINATION 4_1
     *  a S (q|!(AUB)) =>* !((!q & (AUB) & !a) S !a) & Oa
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination4_v1(ArrayList<Formula> fms, Operator op){

        // !(((!q & (AUB)) & !a) S !a) & Oa
        return new BinaryFormula(
                AND,
                // !(((!q&(AUB)) & !a) S !a)
                new BinaryFormula(
                        op, // S
                        subformulaPattern17(fms, op), // (!q&(AUB)) & !a
                        fms.get(0).deepCopy().negate() // !a
                ).negate(),
                // Oa
                convert(
                        new UnaryFormula (
                                ONCE,
                                fms.get(0).deepCopy(), // a
                                null
                        )
                )
        );

    }

    /** ELIMINATION 4_2
     *  a S (q|!(AUB)) =>* E1 | E2 where:
     *      E1 = a S ((!a & ((!q & !a) S (!a & B))) -> !A)
     *      E2 = ((!q & !a) S (!a & B)) -> !(A|(B&(AUB)))
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q */
    public static BinaryFormula elimination4_v2(ArrayList<Formula> fms, Operator op){

        return new BinaryFormula(
                OR,
                e4_E1(fms, op), // a S ((!a & ((!q & !a) S (!a & B))) -> !A)
                e4_E2(fms, op) // ((!q & !a) S (!a & B)) -> !(A|(B&(AUB)))
        );

    }

    /**  E1 =  a S ((!a & ((!q & !a) S (!a & B))) -> !A) */
    public static BinaryFormula e4_E1(ArrayList<Formula> fms, Operator op) {

        // a S ((!a & ((!q & !a) S (!a & B))) -> !A)
        return new BinaryFormula(
                op, // S
                fms.get(0).deepCopy(), // a
                // (!a & ((!q & !a) S (!a & B))) -> !A
                new BinaryFormula(
                        IMPL,
                        // !a & ((!q & !a) S (!a & B))
                        new BinaryFormula(
                                AND,
                                fms.get(0).deepCopy().negate(), // !a
                                subformulaPattern18(fms, op) // (!q & !a) S (!a & B)
                        ),
                        fms.get(1).deepCopy().negate() // !A
                )
        );

    }

    /**  E2 = ((!q & !a) S (!a & B)) -> !(A|(B&(AUB))) */
    public static BinaryFormula e4_E2(ArrayList<Formula> fms, Operator op) {

        // ((!q & !a) S (!a & B)) -> !(A|(B&(AUB)))
        return new BinaryFormula(
                IMPL,
                subformulaPattern18(fms, op), // (!q & !a) S (!a & B)
                subformulaPattern5(fms, op).negate() // A|(B&(AUB))
        );

    }


    /** ELIMINATION 5
     *  (a&(AUB)) S (q|(AUB)) =>* E = E1 | E2 | E3, where:
     * E1 = (aSB) & (A|(B&(AUB)))
     * E2 = ((A & (aSB)) S !b0) & d
     * E3 = ((A & (aSB)) S !b0) & !d & (!q S !A)
     * where:
     *      b0 = (!A & !B) & (!q S !A)
     *      d = A | (B & (AUB))
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination5(ArrayList<Formula> fms, Operator op){

        return ternaryDisjunction(
                e5_E1(fms, op), // E1 = (aSB) & (A|(B&(AUB))
                e5_E2(fms, op), // E2 = ((A & (aSB)) S !b0) & d
                e5_E3(fms, op) // E3 = ((A & (aSB)) S !b0) & !d & (!q S !A)
        );

    }

    /** E1 = (aSB) & (A|(B&(AUB)) */
    public static BinaryFormula e5_E1(ArrayList<Formula> fms, Operator op){

        // (aSB) & (A|(B&(AUB))
        return new BinaryFormula(
                AND,
                subformulaPattern2(fms, op), // aSB
                subformulaPattern5(fms, op) // A|(B&(AUB))
        );
    }

    /** E2 = ((A & (aSB)) S !b0) & d */
    public static BinaryFormula e5_E2(ArrayList<Formula> fms, Operator op){

        // ((A & (aSB)) S !b0) & d
        return new BinaryFormula(
                AND,
                subformulaPattern10(fms, op), // (A & (aSB)) S !b0
                new_d(fms, op) // d = A | (B & (AUB))
        );
    }

    /** E3 = ((A & (aSB)) S !b0) & !d & (!q S !A) */
    public static BinaryFormula e5_E3(ArrayList<Formula> fms, Operator op){

        return new BinaryFormula(
                AND,
                // ((A & (aSB)) S !b0) & !d
                new BinaryFormula(
                        AND,
                        subformulaPattern10(fms, op), // (A & (aSB)) S !b0
                        new_d(fms, op).negate() // !d = !(A|(B &(AUB)))
                ),
                subformulaPattern11(fms, op) // !q S !A

        );

    }

    /** ELIMINATION 6
     *  (a&!(AUB)) S (q|(AUB)) =>* E = E1 | E2 | E3, where:
     *      E1 = (a S (!A & q)) & !A & !B
     *      E2 =
     *      E3 = (a S (q & !A)) & !A & !(AUB)
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination6(ArrayList<Formula> fms, Operator op) {

        return ternaryDisjunction(
                e6_E1(fms, op), // (a S (!A & q)) & !A & !B
                // E2 = e6_E2(fms) needs further separation using the elimination5
                e6_E2(fms, op),
                e6_E3(fms, op) // ((aS(q&!A)) & !A) & !(AUB)
        );

    }


    /** E1 = (a S (!A & q)) & (!A & !B) */
    public static BinaryFormula e6_E1(ArrayList<Formula> fms, Operator op) {
        return new BinaryFormula(
                AND,
                subformulaPattern8(fms, op), //  a S (q & !A)
                subformulaPattern9(fms) // !A & !B
        );

    }

    /** E2 = (!A & !B & (q|(AUB)) & (aS(!A&q))) S (q|(AUB)) */
    public static BinaryFormula e6_E2(ArrayList<Formula> fms, Operator op){

        // (!A & !B & (a S (q & !A)) & (q | (AUB))) S (q | (AUB))
        return new BinaryFormula(
                op, // S
                // !A & !B & (a S (q & !A)) & (q | (AUB))
                new BinaryFormula(
                        AND,
                        subformulaPattern19(fms, op), // !A & !B & (a S (q & !A))
                        subformulaPattern12(fms, op) // q | (AUB)
                ),
                subformulaPattern12(fms, op) // q | (AUB)

        );
    }


    /** E3 = (a S (q & !A)) & !A & !(AUB) */
    public static BinaryFormula e6_E3(ArrayList<Formula> fms, Operator op){

        // ((aS(q&!A)) & !A) & !(AUB)
        return new BinaryFormula(
                AND,
                // (aS(q&!A)) & !A
                new BinaryFormula(
                        AND,
                        subformulaPattern8(fms, op), // a S (q&!A)
                        fms.get(1).deepCopy().negate() // !A
                ),
                subformulaPattern3(fms, op).negate() // !(AUB)
        );

    }


    /** ELIMINATION 7
     *   (a&(AUB)) S (q|!(AUB)) =>* E = E1 | E2 | E3, where:
     *      E1 = (A & (q|!(AUB)) & (aS(B&q))) S (q | !(AUB))
     *      E2 = (aS(B&q)) & A
     *      E3 = (aS(B&q)) & B & (AUB)
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */
    public static BinaryFormula elimination7(ArrayList<Formula> fms, Operator op) {

        return ternaryDisjunction(
                e7_E1(fms, op), // ((A & (q|!(AUB))) & (aS(B&q))) S (q|!(AUB))
                e7_E2(fms, op),  // (aS(B&q)) & A
                e7_E3(fms, op) // (aS(B&q)) & B & (AUB)

        );
    }


    /** E1 = (A & (q|!(AUB)) & (aS(B&q))) S (q | !(AUB)) */
    public static BinaryFormula e7_E1(ArrayList<Formula> fms, Operator op){

        // A & (q|!(AUB))
        BinaryFormula and1 = new BinaryFormula(
                AND,
                fms.get(1).deepCopy(), // A
                subformulaPattern13(fms, op) //  q|!(AUB)
        );

        // (A & (q|!(AUB))) & (aS(B&q))
        BinaryFormula and2 = new BinaryFormula(
                AND,
                and1, // A & (q|!(AUB))
                subformulaPattern14(fms, op) // aS(B&q)
        );

        // ((A & (q|!(AUB))) & (aS(B&q))) S (q|!(AUB))
        return new BinaryFormula(
                op, // S
                and2, // (A & (q|!(AUB))) & (aS(B&q))
                subformulaPattern13(fms, op) // q|!(AUB)
        );

    }

    /** (aS(B&q)) & A */
    public static BinaryFormula e7_E2(ArrayList<Formula> fms, Operator op){
        // (aS(B&q)) & A
        return new BinaryFormula(
                AND,
                subformulaPattern14(fms, op), // aS(B&q)
                fms.get(1).deepCopy() // A
        );
    }

    /** (aS(B&q)) & B & (AUB) */
    public static BinaryFormula e7_E3(ArrayList<Formula> fms, Operator op){

        // ((aS(B&q)) & B) & (AUB)
        return new BinaryFormula(
                AND,
                // (aS(B&q)) & B
                new BinaryFormula(
                        AND,
                        subformulaPattern14(fms, op), // aS(B&q)
                        fms.get(2).deepCopy() // B
                ),
                subformulaPattern3(fms, op) // AUB
        );

    }

    /** ELIMINATION 8
     *   (a&!(AUB)) S (q|!(AUB)) =>* E = E1 | E2 | E3, where:
     *      E1 = !( !(!a|(AUB)) S true)
     *      E2 = (!q & (AUB) & !a) S (!a|(AUB))
     *      E3 = (!q&(AUB)) S (!a|(AUB))
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q
     */

    public static BinaryFormula elimination8(ArrayList<Formula> fms, Operator op) {
        return new BinaryFormula(
                OR,
                convert(e8_E1(fms, op)), // !( !(!a|(AUB)) S true)
                new BinaryFormula(
                        OR,
                        // E2 needs further separation using the elimination5!
                        e8_E2(fms, op), // ((!q & (AUB)) & !a) S (!a|(AUB))
                        // E3 needs further separation using the elimination5!
                        e8_E3(fms, op) // (!q&(AUB)) S (!a|(AUB))

                )
        );

    }

    /** E1 = H(!a|(AUB)) */
    public static UnaryFormula e8_E1(ArrayList<Formula> fms, Operator op) {

        // H(!a|(AUB))
        return new UnaryFormula(
                HIST,
                (Formula) subformulaPattern15(fms, op), // !a|(AUB)
                null
        );

    }

    /** E2 =  (!q & (AUB) & !a) S (!a|(AUB)) */
    public static BinaryFormula e8_E2(ArrayList<Formula> fms, Operator op) {

        // ((!q & (AUB)) & !a) S (!a|(AUB))
        return new BinaryFormula(
                op, // S
                // (!q & (AUB)) & !a
                new BinaryFormula(
                        AND,
                        subformulaPattern16(fms, op), // !q&(AUB)
                        fms.get(0).deepCopy().negate() // !a
                ),
                subformulaPattern15(fms, op) // !a|(AUB)
        );

    }

    /** E3 = (!q&(AUB)) S (!a|(AUB))  */
    public static BinaryFormula e8_E3(ArrayList<Formula> fms, Operator op) {

        // (!q&(AUB)) S (!a|(AUB))
        return new BinaryFormula(
                op, // S
                subformulaPattern16(fms, op), // !q&(AUB)
                subformulaPattern15(fms, op) // !a|(AUB)
        );

    }


    /** @return Returns a new instantiated formula of the form: ((!q & !a)S(!a & !A)) & !A & !B
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_c(ArrayList<Formula> fms, Operator op) {

        // ((!q & !a)S(!a & !A)) & (!A & !B)
        return new BinaryFormula(
                AND,
                subformulaPattern7(fms, op), // (!q & !a) S (!a & !A)
                subformulaPattern9(fms) // !A & !B
        );

    }

    /** @return Returns a new instantiated formula of the form: !A & !B & (!q S !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_b0(ArrayList<Formula> fms, Operator op){

        // (!A & !B) & (!q S !A)
        return new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                subformulaPattern11(fms, op) // !q S !A
        );

    }

    /** @return Returns a new instantiated formula of the form: A | (B & (AUB))
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_d(ArrayList<Formula> fms, Operator op){
        return subformulaPattern5(fms, op); // A | (B & (AUB))
    }


    /** @return Returns a new BinaryFormula of the form: (f1 | f2) | f3
     * @param f1 the first operand of the first OR
     * @param f2 the second operand of the first OR
     * @param f3 the second operand of the second OR
     * */
    public static BinaryFormula ternaryDisjunction(Formula f1, Formula f2, Formula f3){
        return new BinaryFormula(
                OR,
                new BinaryFormula(OR, f1, f2),
                f3
        );
    }


    /** @return Returns a new BinaryFormula which is the disjunction of the formulas got in input
     * @param fms the formulas that will be the operands of the disjunction
     **/
    public static BinaryFormula ennaryDisjunction(ArrayList<Formula> fms) {
        if(fms.size() < 2) throw new IllegalArgumentException(
                String.format("The list of formulas should have size at least two to be possible to create a new disjunction formula")
        );
        Formula first = fms.get(0);
        BinaryFormula result = null;
        for (int i = 1; i < fms.size(); i++) {
            result = new BinaryFormula(
                    OR,
                    first.deepCopy(),
                    fms.get(i)
            );
            first = result;
        }
        return result;
    }

}
