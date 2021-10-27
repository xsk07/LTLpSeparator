package separator;

import formula.BinaryFormula;
import formula.Formula;
import formula.Operator;

import static formula.Operator.*;

public abstract class Lemmas {


    /** LEMMA A.1
      * (AUB) & (CUD) =>* E = E1 | E2 | E3
      * E1 = (A&C)U(B&D)
      * E2 = (A&D&(CUD))U(B&D)
      * E3 = ((C&B&(AUB))U(B&D)) */
    public static BinaryFormula lemmaA1(BinaryFormula f) {

        if(!(f.getOperator().equals(AND) | f.getOperator().equals(OR)) ) {
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula should be & or | but is %s",
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

        return new BinaryFormula(
                OR,
                // ((A&C)U(B&D)) | ((A&D&(CUD))U(B&D))
                new BinaryFormula(
                        OR, // |
                        lemmaA1_E1(fms, op), // (A&C)U(B&D)
                        lemmaA1_E2(fms, op)  // (A&D&(CUD))U(B&D)
                ),
                lemmaA1_E3(fms, op) // (C&B&(AUB))U(B&D)
        );
    }

    /** E1 = (A&C)U(B&D) */
    private static BinaryFormula lemmaA1_E1(Formula[] fms, Operator op){

        // (A&C)U(B&D)
        return new BinaryFormula(
                op,  // U
                // A&C
                new BinaryFormula(
                        AND, // &
                        fms[0].deepCopy(), // A
                        fms[2].deepCopy() // C
                ),
                // B&D
                new BinaryFormula(
                        AND, // &
                        fms[1].deepCopy(), // B
                        fms[3].deepCopy() // D
                )
        );
    }

    /** E2 = (A&D&(CUD))U(B&D) */
    private static BinaryFormula lemmaA1_E2(Formula[] fms, Operator op){

        // (A&D&(CUD))U(B&D)
        return new BinaryFormula(
                op, // U
                // A&D&(CUD)
                new BinaryFormula(
                        AND, // &
                        // A&D
                        new BinaryFormula(
                                AND, // &
                                fms[0].deepCopy(), // A
                                fms[3].deepCopy() // D
                        ),
                        // CUD
                        new BinaryFormula(
                                op, // U
                                fms[2].deepCopy(), // C
                                fms[3].deepCopy() // D
                        )
                ),
                // B&D
                new BinaryFormula(
                        AND, // &
                        fms[1].deepCopy(), // B
                        fms[3].deepCopy() // D
                )
        );
    }

    /** E3 = ((C&B&(AUB))U(B&D)) */
    private static BinaryFormula lemmaA1_E3(Formula[] fms, Operator op){

        // (C&B&(AUB))U(B&D)
        return new BinaryFormula(
                op, // U
                // C&B&(AUB)
                new BinaryFormula(
                        AND, // &
                        // C&B
                        new BinaryFormula(
                                AND, // &
                                fms[2].deepCopy(),  // C
                                fms[1].deepCopy() // B
                        ),
                        // AUB
                        new BinaryFormula(
                                op, // U
                                fms[0].deepCopy(), // A
                                fms[1].deepCopy() // B
                        )
                ),
                // B&D
                new BinaryFormula(
                        AND, // &
                        fms[1], // B
                        fms[3] // D
                )
        );
    }


    public static boolean needsLemmaA2(BinaryFormula f) {

        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula should be S or U but is %s",
                            f.getOperator()
                    )
            );
        }
        return (needsLemmaA2AND(f) || needsLemmaA2OR(f));
    }

    public static boolean needsLemmaA2OR(BinaryFormula f) {
        return (
                (f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))
                        && f.getLoperand().isOperator(OR)
        );
    }

    public static boolean needsLemmaA2AND(BinaryFormula f) {
        return (
                (f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))
                        && f.getRoperand().isOperator(AND)
        );
    }

    /** ((A|B) U C) =>* (AUC) | (BUC) */
    public static BinaryFormula lemmaA2(BinaryFormula f) {

        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula should be S or U but is %s",
                            f.getOperator()
                    )
            );
        }

        // ((A|B) U C) =>* (AUC) | (BUC)
        if(f.getLoperand().isOperator(OR)) return lemmaA2OR(f);

        // (A U (B&C)) =>* (AUB) & (AUC)
        if(f.getRoperand().isOperator(AND)) return lemmaA2AND(f);

        else throw new IllegalArgumentException(
                String.format(
                        "The operator of the left child of the formula should be an OR but is %s " +
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
                            "The operator of the formula should be S or U but is %s",
                            f.getOperator()
                    )
            );
        }

        // ((A|B) U C) =>* (AUC) | (BUC)
        if(f.getLoperand().isOperator(OR)) {
            BinaryFormula lf = (BinaryFormula) f.getLoperand();
            System.out.println("LemmaA2");
            // (AUC)|(BUC)
            return new BinaryFormula (
                    OR,
                    // AUC
                    new BinaryFormula(
                            f.getOperator(), // U
                            lf.getLoperand(), // A
                            f.getRoperand().deepCopy() // C
                    ),
                    // BUC
                    new BinaryFormula(
                            f.getOperator(), // U
                            lf.getRoperand(), // B
                            f.getRoperand().deepCopy() // C
                    )
            );
        }


        else throw new IllegalArgumentException (
                "The operator of the left child of the formula should be an OR"
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


        // (A U (B&C)) =>* (AUB) & (AUC)
        if(f.getRoperand().isOperator(AND)){
            BinaryFormula rf = (BinaryFormula) f.getRoperand();
            System.out.println("LemmaA2");
            // (AUB) & (AUC)
            return new BinaryFormula(
                    AND,
                    // AUB
                    new BinaryFormula(
                            f.getOperator(), // U
                            f.getLoperand().deepCopy(), // A
                            rf.getLoperand() // B
                    ),
                    // AUC
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
        boolean sameLeftChild = lc.getLoperand().equals(rc.getLoperand());
        return operatorIsAnd && sameLeftChild;
    }

    private static boolean needsOrReversedLemmaA2(BinaryFormula f) {
        BinaryFormula lc = (BinaryFormula) f.getLoperand();
        BinaryFormula rc = (BinaryFormula) f.getRoperand();
        boolean operatorIsOr = f.getOperator().equals(OR);
        boolean sameRightChild = lc.getRoperand().equals(rc.getRoperand());
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

        if(!(lc.getLoperand().equals(rc.getLoperand()))) {
            throw new IllegalArgumentException (
                    "The first operator of the two children had to be the same"
            );
        }

        Operator op = f.getOperator();

        if (op.equals(AND)) return andReversedLemmaA2(f);
        if(op.equals(OR))return orReversedLemmaA2(f);
        return f;



    }

    /** (aUb)&(aUc) =>* aU(b&c) */
    private static BinaryFormula andReversedLemmaA2(BinaryFormula f) {
        BinaryFormula lc = (BinaryFormula) f.getLoperand(); // aUb
        BinaryFormula rc = (BinaryFormula) f.getRoperand(); // aUc
        Operator op = lc.getOperator(); // U
        // aU(b&c)
        return new BinaryFormula (
                op, // U
                lc.getLoperand(), // a
                // b&c
                new BinaryFormula(
                        f.getOperator(),  // &
                        lc.getRoperand(), // b
                        rc.getRoperand()  // c
                )
        );
    }

    /** (aUc)|(bUc) =>* (a|b)Uc */
    private static BinaryFormula orReversedLemmaA2(BinaryFormula f) {
        BinaryFormula lc = (BinaryFormula) f.getLoperand(); // aUc
        BinaryFormula rc = (BinaryFormula) f.getRoperand(); // bUc
        Operator op = lc.getOperator(); // U
        // (a|b)Uc
        return new BinaryFormula (
                op, // U
                // a|b
                new BinaryFormula(
                        f.getOperator(),  // |
                        lc.getLoperand(), // a
                        rc.getLoperand()  // b
                ),
                lc.getRoperand() // c
        );
    }


}
