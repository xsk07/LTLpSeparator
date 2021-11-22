package trashbin;

import formula.BinaryFormula;
import formula.Formula;
import formula.Operator;
import static formula.Operator.*;
import static formula.Operator.IMPL;
import static separator.eliminator.SubformulaePatterns.subformulaPattern18;
import static separator.eliminator.SubformulaePatterns.subformulaPattern5;

public class EliminationRule4_V2 {

    /** ELIMINATION 4_2
     *  a S (q|!(AUB)) =>* E1 | E2 where:
     *      E1 = a S ((!a & ((!q & !a) S (!a & B))) -> !A)
     *      E2 = ((!q & !a) S (!a & B)) -> !(A|(B&(AUB)))
     * @param fms The subformulas needed for the elimination.
     * Where: fms[0] == a, fms[1] == A, fms[2] == B, fms[3] == q */
    public static BinaryFormula elimination4_v2(Formula[] fms, Operator op){

        return new BinaryFormula(
                OR,
                e4_E1(fms, op), // a S ((!a & ((!q & !a) S (!a & B))) -> !A)
                e4_E2(fms, op) // ((!q & !a) S (!a & B)) -> !(A|(B&(AUB)))
        );

    }

    /**  E1 =  a S ((!a & ((!q & !a) S (!a & B))) -> !A) */
    public static BinaryFormula e4_E1(Formula[] fms, Operator op) {

        // a S ((!a & ((!q & !a) S (!a & B))) -> !A)
        return new BinaryFormula(
                op, // S
                fms[0].deepCopy(), // a
                // (!a & ((!q & !a) S (!a & B))) -> !A
                new BinaryFormula(
                        IMPL,
                        // !a & ((!q & !a) S (!a & B))
                        new BinaryFormula(
                                AND,
                                fms[0].deepCopy().negate(), // !a
                                subformulaPattern18(fms, op) // (!q & !a) S (!a & B)
                        ),
                        fms[1].deepCopy().negate() // !A
                )
        );

    }

    /**  E2 = ((!q & !a) S (!a & B)) -> !(A|(B&(AUB))) */
    public static BinaryFormula e4_E2(Formula[] fms, Operator op) {

        // ((!q & !a) S (!a & B)) -> !(A|(B&(AUB)))
        return new BinaryFormula(
                IMPL,
                subformulaPattern18(fms, op), // (!q & !a) S (!a & B)
                subformulaPattern5(fms, op).negate() // A|(B&(AUB))
        );
    }


}
