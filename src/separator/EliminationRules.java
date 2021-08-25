package separator;

import formula.AtomicFormula;
import formula.BinaryFormula;
import formula.Formula;
import formula.UnaryFormula;

import java.util.ArrayList;
import static formula.AtomConstant.*;
import static formula.Operator.*;
import static separator.SubformulasPatterns.*;

public abstract class EliminationRules {

    /** ELIMINATION 1
     * (a&(AUB))Sq =>* E = E1 | E2 | E3
     * E1 = aSq & aSB & B & AUB
     * E2 = A & aS(B&q)
     * E3 = (A & q & aSB & aSq )Sq
     * E = (aSq & aSB & B & AUB) | (A & aS(B&q)) | ((A & q & aSB & aSq )Sq)
     * @param fms The subformulas needed for the elimination. With the following correspondences:
     *            fms[0] == a
     *            fms[1] == A
     *            fms[2] == B
     *            fms[3] == q
     */
    public static BinaryFormula elimination1(ArrayList<Formula> fms) {

        return ternaryDisjunction(
                e1_E1(fms), // (((a S q) & (a S B)) & B) & (AUB)
                e1_E2(fms), // A&(aS(B&q))
                e1_E3(fms) // ((A&q & aSB & aSq) S q)
        );

    }


    /** E1 = (((a S q) & (a S B)) & B) & AUB */
    private static BinaryFormula e1_E1(ArrayList<Formula> fms){

        // (aSq)&(aSB)
        BinaryFormula and3_E1 = new BinaryFormula(
                AND,
                subformulaPattern1(fms), // aSq
                subformulaPattern2(fms) // aSB
        );

        // ((aSq)&(aSB))&B
        BinaryFormula and2_E1 = new BinaryFormula(
                AND,
                and3_E1, // (aSq)&(aSB)
                fms.get(2).deepCopy() // B
        );

        // (((a S q) & (a S B)) & B) & (AUB)
        BinaryFormula and1_E1 = new BinaryFormula(
                AND,
                and2_E1, // ((aSq)&(aSB))&B
                subformulaPattern3(fms) // AUB
        );

        return and1_E1;

    }

    /** E2 = A&(aS(B&q)) */
    public static BinaryFormula e1_E2(ArrayList<Formula> fms){

        return new BinaryFormula(
                AND,
                fms.get(1).deepCopy(), // A
                subformulaPattern14(fms) // aS(B&q)
        );

    }

    /** E3 = (A & q & aSB & aSq )Sq */
    public static BinaryFormula e1_E3(ArrayList<Formula> fms){

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
                subformulaPattern2(fms) // aSB
        );

        // A&q & aSB & aSq
        BinaryFormula and3_E3 = new BinaryFormula(
                AND,
                and2_E3, // A&q & aSB
                subformulaPattern1(fms) // aSq
        );

        // (A&q & aSB & aSq) S q
        BinaryFormula since3_E3 = new BinaryFormula(
                SINCE,
                and3_E3, // A&q & aSB & aSq
                fms.get(3).deepCopy() // q
        );

        return since3_E3;
    }

    /** ELIMINATION 2
     * (a&!(AUB))Sq =>* E = E1 | E2 | E3
     * E1 = (a S (q & !A)) & !A & !(AUB)
     * E2 = !A & !B & (aS(!A&q))
     * E3 = (!A & !B & q & (aS(!A&q))) S q
     * @param fms The subformulas needed for the elimination. With the following correspondences:
     *            fms[0] == a
     *            fms[1] == A
     *            fms[2] == B
     *            fms[3] == q
     */
    public static BinaryFormula elimination2(ArrayList<Formula> fms) {

        return ternaryDisjunction(
                e2_E1(fms), // (a S (q & !A)) & !A & !(AUB)
                e2_E2(fms), // !A & !B & (aS(!A&q))
                e2_E3(fms) // (!A & !B & q & (aS(!A&q))) S q
        );

    }

    /** E1 = (a S (q & !A)) & !A & !(AUB) */
    public static BinaryFormula e2_E1(ArrayList<Formula> fms){

        // (a S (q & !A)) & !A
        BinaryFormula and2_E1 = new BinaryFormula(
                AND,
                subformulaPattern8(fms), // a S (q & !A)
                fms.get(1).deepCopy().negate() // !A
        );

        // ((a S (q & !A)) & !A) & !(AUB)
        BinaryFormula and3_E1 = new BinaryFormula(
                AND,
                and2_E1, // (a S (q & !A)) & !A
                subformulaPattern3(fms).negate() // !(AUB)
        );

        return and3_E1;
    }

    /** E2 = !A & !B & (aS(!A&q)) */
    public static BinaryFormula e2_E2(ArrayList<Formula> fms){

        // (!A & !B) & (a S (q & !A))
        return new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                subformulaPattern8(fms) // a S (q & !A)
        );

    }

    /** E3 = (!A & !B & q & (aS(!A&q))) S q */
    public static BinaryFormula e2_E3(ArrayList<Formula> fms){

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
                subformulaPattern8(fms) // aS(!A&q)
        );

        BinaryFormula since = new BinaryFormula(
                SINCE,
                and3, // ((!A & !B) & q) & (aS(!A&q))
                fms.get(3).deepCopy() // q
        );

        return since;

    }


    /** ELIMINATION 3
     * aS(q|(AUB)) =>* E = E1 | E2 | E3
     * E1 = a S false
     * E2 = (A | (B & (AUB))) & (a S (!a & !c))
     * E3 = !(A & (B & (AUB))) & (a S (!a & !c)) & ((!q & !a)S(!a & !A))
     * E = (a S false) | ((A | (B & (AUB))) & (a S (!a & !c))) | !(A & (B & (AUB))) & (a S (!a & !c)) & ((!q & !a)S(!a & !A))
     * c = ((!q & !a)S(!a & !A) & !A & !B)
     * @param fms The subformulas needed for the elimination. With the following correspondences:
     *            fms[0] == a
     *            fms[1] == A
     *            fms[2] == B
     *            fms[3] == q
     */
    public static BinaryFormula elimination3(ArrayList<Formula> fms) {

        return ternaryDisjunction(
                e3_E1(fms), // a S false
                e3_E2(fms), // (A | (B & (AUB))) & (a S (!a & !c))
                e3_E3(fms) // !(A | (B & (AUB))) & (a S (!a & !c)) & ((!q & !a)S(!a & !A))
        );

    }

    /** E1 = a S false */
    private static BinaryFormula e3_E1(ArrayList<Formula> fms){

        return new BinaryFormula(
                SINCE,
                fms.get(0).deepCopy(), // a
                new AtomicFormula(FALSE) // false
        );
    }

    /** @return Returns the 2nd formula of the disjunction of the Elimination 2 */
    private static BinaryFormula e3_E2(ArrayList<Formula> fms){
        /* E2 = (A | (B & (AUB))) & (a S (!a & !c)) */
        return new BinaryFormula(
                AND,
                subformulaPattern5(fms), // A | (B & (AUB))
                subformulaPattern6(fms) // a S (!a & !c)
        );
    }

    /** @return Returns the 3nd formula of the disjunction of the Elimination 2 */
    private static BinaryFormula e3_E3(ArrayList<Formula> fms){
        /* E3 = !(A | (B & (AUB))) & (a S (!a & !c)) & ((!q & !a)S(!a & !A)) */

        // !(A | (B & (AUB))) & (a S (!a & !c))
        BinaryFormula and2_E3 = new BinaryFormula(
                AND,
                subformulaPattern5(fms).negate(), // !(A | (B & (AUB)))
                subformulaPattern6(fms) // a S (!a & !c)
        );

        BinaryFormula and4_E3 = new BinaryFormula(
                AND,
                and2_E3,
                subformulaPattern7(fms) // (!q & !a) S (!a & !A)
        );

        return and4_E3;
    }

    /** ELIMINATION 5
     *  (a&(AUB)) S (q|(AUB)) =>* E = E1 | E2 | E3
     * E1 = (aSB) & (A|(B&(AUB))
     * E2 = ((A & (aSB)) S !b0) & d
     * E3 = ((A & (aSB)) S !b0) & !d & (!q S !A)
     * where:
     *      b0 = (!A & !B) | (!q S !A)
     *      d = A | (B & (AUB))
     * @param fms The subformulas needed for the elimination. With the following correspondences:
     *            fms[0] == a
     *            fms[1] == A
     *            fms[2] == B
     *            fms[3] == q
     */
    public static BinaryFormula elimination5(ArrayList<Formula> fms){

        return ternaryDisjunction(
                e5_E1(fms), // E1 = (aSB) & (A|(B&(AUB))
                e5_E2(fms), // E2 = ((A & (aSB)) S !b0) & d
                e5_E3(fms) // E3 = ((A & (aSB)) S !b0) & !d & (!q S !A)
        );

    }

    /** E1 = (aSB) & (A|(B&(AUB)) */
    public static BinaryFormula e5_E1(ArrayList<Formula> fms){

        // (aSB) & (A|(B&(AUB))
        return new BinaryFormula(
                AND,
                subformulaPattern2(fms), // aSB
                subformulaPattern5(fms) // A|(B&(AUB))
        );
    }

    /** E2 = ((A & (aSB)) S !b0) & d */
    public static BinaryFormula e5_E2(ArrayList<Formula> fms){

        // ((A & (aSB)) S !b0) & d
        return new BinaryFormula(
                AND,
                subformulaPattern10(fms), // (A & (aSB)) S !b0
                new_d(fms) // d = A | (B & (AUB))
        );
    }

    /** E3 = ((A & (aSB)) S !b0) & !d & (!q S !A) */
    public static BinaryFormula e5_E3(ArrayList<Formula> fms){

        // ((A & (aSB)) S !b0) & !d
        BinaryFormula and2 = new BinaryFormula(
                AND,
                subformulaPattern10(fms), // (A & (aSB)) S !b0
                new_d(fms).negate() // !d = !(A|(B &(AUB)))
        );

        BinaryFormula and3 = new BinaryFormula(
                AND,
                and2, // ((A & (aSB)) S !b0) & !d
                subformulaPattern11(fms) // !q S !A
        );

        return and3;
    }

    /** ELIMINATION 6
     *  (a&!(AUB)) S (q|(AUB)) =>* E = E1 | E2 | E3
     * E1 = (a S (!A & q)) & !A & !B
     * E2 = (!A & !B & (q|(AUB)) & (aS(!A&q))) S (q|(AUB))
     * E3 = (a S (q & !A)) & !A & !(AUB)
     * @param fms The subformulas needed for the elimination. With the following correspondences:
     *            fms[0] == a
     *            fms[1] == A
     *            fms[2] == B
     *            fms[3] == q
     */
    public static BinaryFormula elimination6(ArrayList<Formula> fms) {

        return ternaryDisjunction(
                e6_E1(fms), // (a S (!A & q)) & !A & !B
                // E2 = e6_E2(fms) needs further separation using the elimination5
                e6_E2(fms), // ((!A & !B) & (q|(AUB)) & (aS(q&!A))) S (q|(AUB))
                e6_E3(fms) // ((aS(q&!A)) & !A) & !(AUB)
        );

    }


    /** E1 = (a S (!A & q)) & (!A & !B) */
    public static BinaryFormula e6_E1(ArrayList<Formula> fms) {
        return new BinaryFormula(
                AND,
                subformulaPattern8(fms), //  a S (q & !A)
                subformulaPattern9(fms) // !A & !B
        );

    }


    /** E2 = (!A & !B & (q|(AUB)) & (aS(!A&q))) S (q|(AUB)) */
    public static BinaryFormula e6_E2(ArrayList<Formula> fms){

        // (!A & !B) & (q|(AUB))
        BinaryFormula and1 = new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                subformulaPattern12(fms) // q|(AUB)
        );

        // (!A & !B) & (q|(AUB)) & (aS(q&!A))
        BinaryFormula and3 = new BinaryFormula(
                AND,
                and1, // (!A & !B) & (q|(AUB))
                subformulaPattern8(fms) // aS(q&!A)

        );

        // ((!A & !B) & (q|(AUB)) & (aS(q&!A))) S (q|(AUB))
        BinaryFormula since2 = new BinaryFormula(
                SINCE,
                and3, // (!A & !B) & (q|(AUB)) & (aS(q&!A))
                subformulaPattern12(fms) // q|(AUB)
        );

        return since2;

    }


    /** E3 = (a S (q & !A)) & !A & !(AUB) */
    public static BinaryFormula e6_E3(ArrayList<Formula> fms){

        // (aS(q&!A)) & !A
        BinaryFormula and2 = new BinaryFormula(
                AND,
                subformulaPattern8(fms), // a S (q&!A)
                fms.get(1).deepCopy().negate() // !A
        );

        // ((aS(q&!A)) & !A) & !(AUB)
        BinaryFormula and3 = new BinaryFormula(
                AND,
                and2, // (aS(q&!A)) & !A
                subformulaPattern3(fms).negate() // !(AUB)
        );

        return and3;

    }


    /** ELIMINATION 7
     *   =>* E = E1 | E2 | E3, where:
     * E1 = (A & (q|!(AUB)) & (aS(B&q))) S (q | !(AUB))
     * E2 = (aS(B&q)) & A
     * E3 = (aS(B&q)) & B & (AUB)
     * @param fms The subformulas needed for the elimination. With the following correspondences:
     *            fms[0] == a
     *            fms[1] == A
     *            fms[2] == B
     *            fms[3] == q
     */
    public static BinaryFormula elimination7(ArrayList<Formula> fms) {

        return ternaryDisjunction(
                e7_E1(fms), // ((A & (q|!(AUB))) & (aS(B&q))) S (q|!(AUB))
                e7_E2(fms),  // (aS(B&q)) & A
                e7_E3(fms) // (aS(B&q)) & B & (AUB)

        );
    }


    /** E1 = (A & (q|!(AUB)) & (aS(B&q))) S (q | !(AUB)) */
    public static BinaryFormula e7_E1(ArrayList<Formula> fms){

        // A & (q|!(AUB))
        BinaryFormula and1 = new BinaryFormula(
                AND,
                fms.get(1).deepCopy(), // A
                subformulaPattern13(fms) //  q|!(AUB)
        );

        // (A & (q|!(AUB))) & (aS(B&q))
        BinaryFormula and2 = new BinaryFormula(
                AND,
                and1, // A & (q|!(AUB))
                subformulaPattern14(fms) // aS(B&q)
        );

        // ((A & (q|!(AUB))) & (aS(B&q))) S (q|!(AUB))
        BinaryFormula since2 = new BinaryFormula(
                SINCE,
                and2, // (A & (q|!(AUB))) & (aS(B&q))
                subformulaPattern13(fms) // q|!(AUB)
        );

        return since2;
    }

    /** (aS(B&q)) & A */
    public static BinaryFormula e7_E2(ArrayList<Formula> fms){
        // (aS(B&q)) & A
        return new BinaryFormula(
                AND,
                subformulaPattern14(fms), // aS(B&q)
                fms.get(1).deepCopy() // A
        );
    }

    /** (aS(B&q)) & B & (AUB) */
    public static BinaryFormula e7_E3(ArrayList<Formula> fms){

        // (aS(B&q)) & B
        BinaryFormula and2 = new BinaryFormula(
                AND,
                subformulaPattern14(fms), // aS(B&q)
                fms.get(2).deepCopy() // B
        );

        // ((aS(B&q)) & B) & (AUB)
        BinaryFormula and3 = new BinaryFormula(
                AND,
                and2, // (aS(B&q)) & B
                subformulaPattern3(fms) // AUB
        );

        return and3;
    }

    /** ELIMINATION 8
     *   =>* E = E1 | E2 | E3, where:
     * E1 = H(!a|(AUB))
     * E2 = (!q & (AUB) & !a) S (!a|(AUB))
     * E3 = (!q&(AUB)) S (!a|(AUB))
     * @param fms The subformulas needed for the elimination. With the following correspondences:
     *            fms[0] == a
     *            fms[1] == A
     *            fms[2] == B
     *            fms[3] == q
     */
    public static BinaryFormula elimination8(ArrayList<Formula> fms) {

        return  ternaryDisjunction(

                // E1 needs application of ruleH!
                e8_E1(fms), // H(!a|(AUB))
                // E2 needs further separation using the elimination5!
                e8_E2(fms), // ((!q & (AUB)) & !a) S (!a|(AUB))
                // E3 needs further separation using the elimination5!
                e8_E3(fms) // (!q&(AUB)) S (!a|(AUB))
        );

    }

    /** E1 = H(!a|(AUB)) */
    public static UnaryFormula e8_E1(ArrayList<Formula> fms) {

        // H(!a|(AUB))
        return new UnaryFormula(
                HIST,
                subformulaPattern15(fms) // !a|(AUB)
        );

    }

    /** E2 =  (!q & (AUB) & !a) S (!a|(AUB)) */
    public static BinaryFormula e8_E2(ArrayList<Formula> fms) {

        // ((!q & (AUB)) & !a) S (!a|(AUB))
        return new BinaryFormula(
                SINCE,
                // (!q & (AUB)) & !a
                new BinaryFormula(
                        AND,
                        subformulaPattern16(fms), // !q&(AUB)
                        fms.get(0).deepCopy() // !a
                ),
                subformulaPattern15(fms) // !a|(AUB)
        );

    }

    /** E3 = (!q&(AUB)) S (!a|(AUB))  */
    public static BinaryFormula e8_E3(ArrayList<Formula> fms) {

        // (!q&(AUB)) S (!a|(AUB))
        return new BinaryFormula(
                SINCE,
                subformulaPattern16(fms), // !q&(AUB)
                subformulaPattern15(fms) // !a|(AUB)
        );

    }


    /** @return Returns a new instantiated formula of the form: ((!q & !a)S(!a & !A)) & !A & !B
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_c(ArrayList<Formula> fms) {

        return new BinaryFormula(
                AND,
                subformulaPattern7(fms), // (!q & !a) S (!a & !A)
                subformulaPattern9(fms) // !A & !B
        );

    }

    /** @return Returns a new instantiated formula of the form: (!A & !B) & (!q S !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_b0(ArrayList<Formula> fms){

        return new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                subformulaPattern11(fms) // !q S !A
        );

    }

    /** @return Returns a new instantiated formula of the form: A | (B & (AUB))
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_d(ArrayList<Formula> fms){
        return subformulaPattern5(fms); // A | (B & (AUB))
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

}
