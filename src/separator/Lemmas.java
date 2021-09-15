package separator;

import formula.BinaryFormula;
import formula.UnaryFormula;
import static formula.Operator.*;
import static separator.OperatorChain.operatorChainSearch;
import static separator.OperatorChain.operatorChainSearchOfNegation;

public class Lemmas {

    public static boolean needsLemmaA2(BinaryFormula f){
        if(!(f.getOperator().equals(SINCE) || f.getOperator().equals(UNTIL))){
            throw new IllegalArgumentException(
                    String.format(
                            "The operator of the formula should be S or U but is %s",
                            f.getOperator()
                    )
            );
        }

        if(f.getLoperand().isOperator(OR)){
            BinaryFormula lf = (BinaryFormula) f.getLoperand();

            BinaryFormula mirrOp = (BinaryFormula) operatorChainSearch(lf, f.getOperator().getMirrorOperator());
            if(mirrOp != null){
                return true;
            }

            BinaryFormula negMirrOp = (BinaryFormula) operatorChainSearchOfNegation(lf, f.getOperator().getMirrorOperator());
            if(negMirrOp != null){
                return true;
            }

            BinaryFormula andInOr = (BinaryFormula) operatorChainSearch(lf, AND);
            if(andInOr != null){
                BinaryFormula mirrorOpInAnd = (BinaryFormula) operatorChainSearch(andInOr, f.getOperator().getMirrorOperator());
                if(mirrorOpInAnd != null){ return true; }
                else{
                    UnaryFormula negMirrorOpInAnd = (UnaryFormula) operatorChainSearchOfNegation(andInOr, f.getOperator().getMirrorOperator());
                    return (negMirrorOpInAnd != null);
                }
            }
        }

        if(f.getRoperand().isOperator(AND)){
            BinaryFormula rf = (BinaryFormula) f.getRoperand();

            BinaryFormula mirrOp = (BinaryFormula) operatorChainSearch(rf, f.getOperator().getMirrorOperator());
            if(mirrOp != null){
                return true;
            }

            BinaryFormula negMirrOp = (BinaryFormula) operatorChainSearchOfNegation(rf, f.getOperator().getMirrorOperator());
            if(negMirrOp != null){
                return true;
            }


            BinaryFormula orInAnd = (BinaryFormula) operatorChainSearch(rf, OR);
            if(orInAnd != null){
                BinaryFormula mirrorOpInOr = (BinaryFormula) operatorChainSearch(orInAnd, f.getOperator().getMirrorOperator());
                if(mirrorOpInOr != null){
                    return true;
                }
                else{
                    BinaryFormula negMirrorOpInAnd = (BinaryFormula) operatorChainSearchOfNegation(orInAnd, f.getOperator().getMirrorOperator());
                    return (negMirrorOpInAnd != null);
                }
            }
        }

        return false;
    }


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
