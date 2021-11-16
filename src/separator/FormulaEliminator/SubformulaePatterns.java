package separator.FormulaEliminator;

import formula.BinaryFormula;
import formula.Formula;
import formula.Operator;
import static formula.Operator.*;

public abstract class SubformulaePatterns {

    /** @return Returns a newly created formula of the form: S(a,q)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern1(Formula[] fms, Operator op){
        // S(a,q)
        return new BinaryFormula(
                op, // S
                fms[0].deepCopy(), // a
                fms[3].deepCopy()  // q
        );
    }

    /** @return Returns a newly created formula of the form: S(a,B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern2(Formula[] fms, Operator op){
        // S(a,B)
        return new BinaryFormula(
                op, // S
                fms[0].deepCopy(), // a
                fms[2].deepCopy()  // B
        );
    }

    /** @return Returns a new instantiated formula of the form: U(A,B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern3(Formula[] fms, Operator op){
        // U(A,B)
        return new BinaryFormula(
                op, // U
                fms[1].deepCopy(),  // A
                fms[2].deepCopy()   // B
        );

    }

    /** @return Returns a new instantiated formula of the form: !q & !a
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern4(Formula[] fms){
        // !q & !a
        return new BinaryFormula(
                AND, // &
                fms[3].deepCopy().negate(), // !q
                fms[0].deepCopy().negate()  // !a
        );

    }

    /** @return Returns a new instantiated formula of the form: A | (B & U(A,B))
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern5(Formula[] fms, Operator op){
        // A | (B & U(A,B))
        return new BinaryFormula(
                OR, // |
                fms[1].deepCopy(), // A
                //(B & U(A,B))
                new BinaryFormula(
                        AND, // &
                        fms[2].deepCopy(), // B
                        subformulaPattern3(fms, op) // U(A,B)
                )
        );
    }

    /** @return Returns a new instantiated formula of the form: S(a, !a & !c)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern6(Formula[] fms, Operator op){
        // S(a, !a & !c)
        return new BinaryFormula(
                op, // S
                fms[0].deepCopy(), // a
                // !a & !c
                new BinaryFormula(
                        AND, // &
                        fms[0].deepCopy().negate(), // !a
                        new_c(fms, op).negate() // !c
                )
        );
    }

    /** @return Returns a new instantiated formula of the form: S(!q & !a, !a & !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern7(Formula[] fms, Operator op) {
        // S(!q & !a, !a & !A)
        return new BinaryFormula(
                op, // S
                subformulaPattern4(fms), // !q & !a
                // !a & !A
                new BinaryFormula(
                    AND, // &
                    fms[0].deepCopy().negate(), // !a
                    fms[1].deepCopy().negate() // !A
                )
        );
    }

    /** @return Returns a new instantiated formula of the form: S(a, q & !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern8(Formula[] fms, Operator op){
        // S(a, q & !A)
        return new BinaryFormula(
                op, // S
                fms[0].deepCopy(), // a
                // q & !A
                new BinaryFormula(
                        AND, // &
                        fms[3].deepCopy(), // q
                        fms[1].deepCopy().negate() // !A
                )
        );
    }

    /** @return Returns a new instantiated formula of the form: !A & !B
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern9(Formula[] fms){
        // !A & !B
        return new BinaryFormula(
                AND, // &
                fms[1].deepCopy().negate(), // !A
                fms[2].deepCopy().negate()  // !B
        );
    }

    /** @return Returns a new instantiated formula of the form: S(A & S(a,B), !b0),
     * where b0 = (!A & !B) & (!q S !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern10(Formula[] fms, Operator op){
        // S(A & S(a,B), !b0)
        return new BinaryFormula(
                op, // S
                // A & S(a,B)
                new BinaryFormula(
                        AND, // &
                        fms[1].deepCopy(), // A
                        subformulaPattern2(fms, op) // S(a,B)
                ),
                new_b0(fms, op).negate() // !b0
        );
    }

    /** @return Returns a new instantiated formula of the form: S(!q, !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern11(Formula[] fms, Operator op){
        // S(!q, !A)
        return new BinaryFormula(
                op, // S
                fms[3].deepCopy().negate(), // !q
                fms[1].deepCopy().negate()  // !A
        );
    }

    /** @return Returns a new instantiated formula of the form: q | U(A,B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern12(Formula[] fms, Operator op){
        // q | U(A,B)
        return new BinaryFormula(
                OR, // |
                fms[3].deepCopy(), // q
                subformulaPattern3(fms, op) // U(A,B)
        );

    }

    /** @return Returns a new instantiated formula of the form: q | !U(A,B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern13(Formula[] fms, Operator op){
        // q | !U(A,B)
        return new BinaryFormula(
                OR, // |
                fms[3].deepCopy(), // q
                subformulaPattern3(fms, op).negate() // !U(A,B)
        );
    }

    /** @return Returns a new instantiated formula of the form: S(a, q & B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern14(Formula[] fms, Operator op){
        // S(a, q & B)
        return new BinaryFormula(
                op, // S
                fms[0].deepCopy(), // a
                // q & B
                new BinaryFormula(
                        AND, // &
                        fms[3].deepCopy(), // q
                        fms[2].deepCopy()  // B
                )
        );
    }

    /** @return Returns a new instantiated formula of the form: !a | U(A,B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern15(Formula[] fms, Operator op){
        // !a | U(A,B)
        return new BinaryFormula(
                OR, // |
                fms[0].deepCopy().negate(), // !a
                subformulaPattern3(fms, op) // U(A,B)
        );
    }

    /** @return Returns a new instantiated formula of the form: !q & U(A,B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern16(Formula[] fms, Operator op){
        // !q & U(A,B)
        return new BinaryFormula(
                AND, // &
                fms[3].deepCopy().negate(), // !q
                subformulaPattern3(fms, op) // U(A,B)
        );
    }

    /** @return Returns a new instantiated formula of the form: !a & !q & U(A,B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern17(Formula[] fms, Operator op){
        //  !a & !q & U(A,B)
        return new BinaryFormula(
                AND, // &
                subformulaPattern4(fms), // !q & !a
                subformulaPattern3(fms, op) // U(A,B)
        );
    }

    /** @return Returns a new instantiated formula of the form: S(!q & !a, !a & B)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern18(Formula[] fms, Operator op){
        // S(!q & !a, !a & B)
        return new BinaryFormula(
                op, // S
                subformulaPattern4(fms), // !q & !a
                // !a & B
                new BinaryFormula(
                        AND, // &
                        fms[0].deepCopy().negate(), // !a
                        fms[2].deepCopy() // B
                )
        );
    }

    /** @return Returns a new instantiated formula of the form: !A & !B & S(a, q & !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula subformulaPattern19(Formula[] fms, Operator op){
        // !A & !B & S(a, q & !A)
        return new BinaryFormula(
                AND, // &
                subformulaPattern9(fms), // !A & !B
                subformulaPattern8(fms, op) // S(a, q & !A)
        );
    }

    /** @return Returns a new instantiated formula of the form: S(!q & !a, !a & !A) & !A & !B
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_c(Formula[] fms, Operator op) {
        // S(!q & !a, !a & !A) & !A & !B
        return new BinaryFormula(
                AND, // &
                subformulaPattern9(fms), // !A & !B
                subformulaPattern7(fms, op) // S(!q & !a, !a & !A)
        );
    }

    /** @return Returns a new instantiated formula of the form: !A & !B & S(!q, !A)
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_b0(Formula[] fms, Operator op){
        // (!A & !B) & S(!q, !A)
        return new BinaryFormula(
                AND,
                subformulaPattern9(fms), // !A & !B
                subformulaPattern11(fms, op) // S(!q, !A)
        );
    }

    /** @return Returns a new instantiated formula of the form: A | (B & U(A,B))
     * @param fms the ArrayList of the subformulas needed by the elimination rules.
     * Where: fms[0] = a, fms[1] = A, fms[2] = B, fms[3] = q */
    public static BinaryFormula new_d(Formula[] fms, Operator op){
        return subformulaPattern5(fms, op); // A | (B & U(A,B))
    }

}
