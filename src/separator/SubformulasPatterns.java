package separator;

import formula.BinaryFormula;
import formula.Formula;
import formula.UnaryFormula;

import java.util.ArrayList;
import static formula.Operator.*;
import static formula.Operator.SINCE;
import static separator.EliminationRules.new_b0;
import static separator.EliminationRules.new_c;

public abstract class SubformulasPatterns {

    /** @return Returns a newly created formula of the form: aSq
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern1(ArrayList<Formula> fms){

        // aSq
        return new BinaryFormula(
                SINCE,
                fms.get(0).deepCopy(), // a
                fms.get(3).deepCopy() // q
        );

    }

    /** @return Returns a newly created formula of the form: aSB
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern2(ArrayList<Formula> fms){

        // aSB
        return new BinaryFormula(
                SINCE,
                fms.get(0).deepCopy(), // a
                fms.get(2).deepCopy() // B
        );

    }

    /** @return Returns a new instantiated formula of the form: AUB
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern3(ArrayList<Formula> fms){

        // AUB
        return new BinaryFormula(
                UNTIL,
                fms.get(1).deepCopy(), // A
                fms.get(2).deepCopy() // B
        );

    }

    /** @return Returns a new instantiated formula of the form: A|(B&(AUB))
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern5(ArrayList<Formula> fms){

        // A|(B&(AUB))
        return new BinaryFormula(
                OR,
                fms.get(1).deepCopy(), // A
                //(B&(AUB))
                new BinaryFormula(
                        AND,
                        fms.get(2).deepCopy(), // B
                        subformulaPattern3(fms) // AUB
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: aS(!a&!c)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern6(ArrayList<Formula> fms){

        // a S (!a&!c)
        return new BinaryFormula(
                SINCE,
                fms.get(0).deepCopy(), // a
                // !a & !c
                new BinaryFormula(
                        AND,
                        fms.get(0).deepCopy().negate(), // !a
                        new_c(fms).negate() // !c
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: (!q&!a)S(!a&!A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern7(ArrayList<Formula> fms){

        // (!q & !a) S (!a & !A)
        return new BinaryFormula(
                SINCE,
                // !q & !a
                new BinaryFormula(
                        AND,
                        fms.get(3).deepCopy().negate(), // !q
                        fms.get(0).deepCopy().negate() // !a
                ),
                // !a & !A
                new BinaryFormula(
                    AND,
                    fms.get(0).deepCopy().negate(), // !a
                    fms.get(1).deepCopy().negate() // !A
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: aS(q&!A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern8(ArrayList<Formula> fms){

        // a S (q & !A)
        return new BinaryFormula(
                SINCE,
                fms.get(0).deepCopy(), // a
                // q & !A
                new BinaryFormula(
                        AND,
                        fms.get(3).deepCopy(), // q
                        fms.get(1).deepCopy().negate() // !A
                )
        );


    }

    /** @return Returns a new instantiated formula of the form: !A & !B
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern9(ArrayList<Formula> fms){

        // !A & !B
        return new BinaryFormula(
                AND,
                fms.get(1).deepCopy().negate(), // !A
                fms.get(2).deepCopy().negate() // !B
        );

    }

    /** @return Returns a new instantiated formula of the form: (A&(aSB)) S !b0,
     * where b0 = (!A & !B) & (!q S !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern10(ArrayList<Formula> fms){

        // (A&(aSB)) S ((!A & !B) & (!q S !A))
        return new BinaryFormula(
                SINCE,
                // A&(aSB)
                new BinaryFormula(
                        AND,
                        fms.get(1).deepCopy(), // A
                        subformulaPattern2(fms) // aSB
                ),
                new_b0(fms) // (!A & !B) & (!q S !A)
        );


    }

    /** @return Returns a new instantiated formula of the form: !q S !A
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern11(ArrayList<Formula> fms){

        // !q S !A
        return new BinaryFormula(
                SINCE,
                fms.get(3).deepCopy().negate(), // !q
                fms.get(1).deepCopy().negate() // !A
        );

    }

    /** @return Returns a new instantiated formula of the form: q|(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern12(ArrayList<Formula> fms){

        // q | (AUB)
        return new BinaryFormula(
                OR,
                fms.get(3).deepCopy(), // q
                subformulaPattern3(fms) // AUB
        );

    }


    /** @return Returns a new instantiated formula of the form: q|!(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern13(ArrayList<Formula> fms){

        // q | !(AUB)
        return new BinaryFormula(
                OR,
                fms.get(3).deepCopy(), // q
                subformulaPattern3(fms).negate() // !(AUB)
        );

    }

    /** @return Returns a new instantiated formula of the form: aS(B&q)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern14(ArrayList<Formula> fms){

        // a S (B & q)
        return new BinaryFormula(
                SINCE,
                fms.get(0).deepCopy(), // a
                // B & q
                new BinaryFormula(
                        AND,
                        fms.get(2).deepCopy(),// B
                        fms.get(3).deepCopy() // q
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: !a|(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */

    public static BinaryFormula subformulaPattern15(ArrayList<Formula> fms){

        // !a|(AUB)
        return new BinaryFormula(
                OR,
                new UnaryFormula(NOT, fms.get(0).deepCopy()), // !a
                subformulaPattern3(fms) // AUB
        );
    }


    /** @return Returns a new instantiated formula of the form: !q&(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */

    public static BinaryFormula subformulaPattern16(ArrayList<Formula> fms){

        // !q&(AUB)
        return new BinaryFormula(
                AND,
                new UnaryFormula(NOT, fms.get(3).deepCopy()), // !q
                subformulaPattern3(fms) // AUB
        );
    }

}
