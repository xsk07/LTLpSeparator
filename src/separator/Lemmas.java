package separator;

import formula.BinaryFormula;
import static formula.Operator.*;

public class Lemmas {

    /** ((A|B) U C) = (AUC) | (BUC) */
    public static BinaryFormula lemmaA2(BinaryFormula f){

        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula should be S or U but is %s",
                            f.getOperator()
                    )
            );
        }

        // ((A|B) U C) = (AUC) | (BUC)
        if(f.getLoperand().isOperator(OR)){
            BinaryFormula lf = (BinaryFormula) f.getLoperand();
            System.out.println("LemmaA2");
            // (AUC)|(BUC)
            return new BinaryFormula(
                    OR,
                    // AUC
                    new BinaryFormula(
                            f.getOperator(), // U
                            lf.getLoperand().deepCopy(), // A
                            f.getRoperand().deepCopy() // C
                    ),
                    // BUC
                    new BinaryFormula(
                            f.getOperator(), // U
                            lf.getRoperand().deepCopy(), // B
                            f.getRoperand().deepCopy() // C
                    )
            );
        }

        // (A U (B&C)) = (AUB) & (AUC)
        if(f.getRoperand().isOperator(AND)){
            BinaryFormula rf = (BinaryFormula) f.getRoperand();
            System.out.println("LemmaA2");
            // (AUB)&(AUC)
            return new BinaryFormula(
                    AND,
                    // AUB
                    new BinaryFormula(
                            f.getOperator(), // U
                            f.getLoperand().deepCopy(), // A
                            rf.getLoperand().deepCopy() // B
                    ),
                    // AUC
                    new BinaryFormula(
                            f.getOperator(), // U
                            f.getLoperand().deepCopy(), // A
                            rf.getRoperand().deepCopy() // C
                    )
            );
        }
        throw new IllegalArgumentException(
                String.format(
                        "The operator of the left child of the formula should be an OR but is %s" +
                                "or the operator of the right child of the formula should be an AND but is %s",
                        f.getLoperand().getImage(),
                        f.getRoperand().getImage()
                )
        );
    }

}
