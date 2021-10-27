package separator;

import formula.BinaryFormula;
import formula.Formula;
import formula.Operator;
import java.util.ArrayList;
import static formula.Operator.*;
import static separator.FormulaEliminator.new_b0;
import static separator.FormulaEliminator.new_c;

public abstract class SubformulasPatterns {


    /** @return Returns a newly created formula of the form: aSq
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern1(ArrayList<Formula> fms, Operator op){

        // aSq
        return new BinaryFormula(
                op, // S
                fms.get(0).deepCopy(), // a
                fms.get(3).deepCopy() // q
        );

    }

    /** @return Returns a newly created formula of the form: aSB
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern2(ArrayList<Formula> fms, Operator op){

        // aSB
        return new BinaryFormula(
                op, // S
                fms.get(0).deepCopy(), // a
                fms.get(2).deepCopy() // B
        );

    }

    /** @return Returns a new instantiated formula of the form: AUB
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern3(ArrayList<Formula> fms, Operator op){

        // AUB
        return new BinaryFormula(
                op.getMirrorOperator(), // U
                fms.get(1).deepCopy(), // A
                fms.get(2).deepCopy() // B
        );

    }

    /** @return Returns a new instantiated formula of the form: !q & !a
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern4(ArrayList<Formula> fms){

        // !q & !a
        return new BinaryFormula(
                AND,
                fms.get(3).deepCopy().negate(), // !q
                fms.get(0).deepCopy().negate() // !a
        );

    }

    /** @return Returns a new instantiated formula of the form: A|(B&(AUB))
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern5(ArrayList<Formula> fms, Operator op){

        // A|(B&(AUB))
        return new BinaryFormula(
                OR,
                fms.get(1).deepCopy(), // A
                //(B&(AUB))
                new BinaryFormula(
                        AND,
                        fms.get(2).deepCopy(), // B
                        subformulaPattern3(fms, op) // AUB
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: aS(!a&!c)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern6(ArrayList<Formula> fms, Operator op){

        // a S (!a & !c)
        return new BinaryFormula(
                op, // S
                fms.get(0).deepCopy(), // a
                // !a & !c
                new BinaryFormula(
                        AND,
                        fms.get(0).deepCopy().negate(), // !a
                        new_c(fms, op).negate() // !c
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: (!q&!a)S(!a&!A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern7(ArrayList<Formula> fms, Operator op){

        // (!q & !a) S (!a & !A)
        return new BinaryFormula(
                op, // S
                subformulaPattern4(fms), // !q & !a
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
    public static BinaryFormula subformulaPattern8(ArrayList<Formula> fms, Operator op){

        // a S (q & !A)
        return new BinaryFormula(
                op, // S
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
    public static BinaryFormula subformulaPattern10(ArrayList<Formula> fms, Operator op){

        // (A&(aSB)) S !((!A & !B) & (!q S !A))
        return new BinaryFormula(
                op, // S
                // A&(aSB)
                new BinaryFormula(
                        AND,
                        fms.get(1).deepCopy(), // A
                        subformulaPattern2(fms, op) // aSB
                ),
                new_b0(fms, op).negate() // !((!A & !B) & (!q S !A))
        );


    }

    /** @return Returns a new instantiated formula of the form: !q S !A
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern11(ArrayList<Formula> fms, Operator op){

        // !q S !A
        return new BinaryFormula(
                op, // S
                fms.get(3).deepCopy().negate(), // !q
                fms.get(1).deepCopy().negate() // !A
        );

    }

    /** @return Returns a new instantiated formula of the form: q|(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern12(ArrayList<Formula> fms, Operator op){

        // q | (AUB)
        return new BinaryFormula(
                OR,
                fms.get(3).deepCopy(), // q
                subformulaPattern3(fms, op) // AUB
        );

    }

    /** @return Returns a new instantiated formula of the form: q|!(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern13(ArrayList<Formula> fms, Operator op){

        // q | !(AUB)
        return new BinaryFormula(
                OR,
                fms.get(3).deepCopy(), // q
                subformulaPattern3(fms, op).negate() // !(AUB)
        );

    }

    /** @return Returns a new instantiated formula of the form: aS(q&B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern14(ArrayList<Formula> fms, Operator op){

        // a S (q & B)
        return new BinaryFormula(
                op, // S
                fms.get(0).deepCopy(), // a
                // q & B
                new BinaryFormula(
                        AND,
                        fms.get(3).deepCopy(), // q
                        fms.get(2).deepCopy()// B
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: !a|(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern15(ArrayList<Formula> fms, Operator op){

        // !a|(AUB)
        return new BinaryFormula(
                OR,
                fms.get(0).deepCopy().negate(), // !a
                subformulaPattern3(fms, op) // AUB
        );
    }


    /** @return Returns a new instantiated formula of the form: !q&(AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern16(ArrayList<Formula> fms, Operator op){

        // !q&(AUB)
        return new BinaryFormula(
                AND,
                fms.get(3).deepCopy().negate(), // !q
                subformulaPattern3(fms, op) // AUB
        );
    }

    /** @return Returns a new instantiated formula of the form: !a & !q & (AUB)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern17(ArrayList<Formula> fms, Operator op){

        //  !a & (!q&(AUB))
        return new BinaryFormula(
                AND,
                fms.get(0).deepCopy().negate(), // !a
                subformulaPattern16(fms, op) // !q&(AUB)
        );

    }

    /** @return Returns a new instantiated formula of the form: (!q & !a) S (!a & B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern18(ArrayList<Formula> fms, Operator op){

        // (!q & !a) S (!a & B)
        return new BinaryFormula(
                op, // S
                subformulaPattern4(fms), // !q & !a
                // !a & B
                new BinaryFormula(
                        AND,
                        fms.get(0).deepCopy().negate(), // !a
                        fms.get(2).deepCopy() // B
                )
        );

    }

    /** @return Returns a new instantiated formula of the form: !A & !B & (a S (q & !A))
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern19(ArrayList<Formula> fms, Operator op){

        // !A & !B & (a S (q & !A))
        return new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                subformulaPattern8(fms, op) // a S (q & !A)
        );

    }


}
