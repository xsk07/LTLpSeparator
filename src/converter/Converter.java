package converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import static converter.ConverterConstants.*;

/** Formulas converter. */
public class Converter {

    public static void Converter(){}

    /**
     * Converts a formula containing unary temporal operators into an equivalent form
     * without them.
     */
    public static void toBinaryForm(ArrayList<String> al) { // al: ArrayList of token images
        for(int i = 0; i < al.size(); i++){
            String ti = al.get(i); // i-th token image
            int f; // the last index of the formula in the scope of the unary operator
            switch (ti){
                case ONCE:
                    f = endQ(al, i);
                    ruleO(al, i, f);
                    break;
                case HIST:
                    f = endQ(al, i);
                    ruleH(al, i, f);
                    break;
                case YEST:
                    f = endQ(al, i);
                    ruleY(al, i, f);
                    break;
                case FIN:
                    f = endQ(al, i);
                    ruleF(al, i, f);
                    break;
                case GLOB:
                    f = endQ(al, i);
                    ruleG(al, i, f);
                    break;
                case NEXT:
                    f = endQ(al, i);
                    ruleX(al, i, f);
                    break;
                default: break;
            }
        }
    }

    /** rewriting rule: O(q) =>* (q S true) */
    public static int ruleO(ArrayList<String> al, int i, int f){
        boolean needPar = needParentheses(al, i, f); // tells if are needed parentheses around a formula
        /* predQ: array list of the token images to add before the first index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList predQ = new ArrayList<String>();
        /* nextQ: array list of the token images to add after the last index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList nextQ = new ArrayList<String>();
        al.remove(i); // deletes the token image relative to the unary operator
        f = removeNeedlessParen(al, i, f);
        if(needPar) { predQ.add("("); f += 1; }
        nextQ.addAll(Arrays.asList("S", "true"));
        if(needPar) { nextQ.add(")"); }
        // updateFormula(al, i, f, predQ, nextQ)
        al.addAll(i, predQ);
        al.addAll(f, nextQ);
        return f;
    }

    /** Rewriting rule: H(q) =>* !(!q S false).
      * @param al An array list of tokens
      * @param i
      * @param f
      * @return Returns the end index of the rewritten formula.
      */
    public static int ruleH(ArrayList<String> al, int i, int f){
        al.remove(i);
        f = removeNeedlessParen(al, i, f);
        /* predQ: array list of the token images to add before the first index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList predQ = new ArrayList<>(Arrays.asList("!", "(", "!")); f += 3;
        /* nextQ: array list of the token images to add after the last index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList nextQ = new ArrayList<>(Arrays.asList("S", "false", ")"));
        al.addAll(i, predQ);
        al.addAll(f, nextQ);
        return f;
    }

    /** rewriting rule: Y(q) =>* (q S false) */
    public static int ruleY(ArrayList<String> al, int i, int f){
        boolean needPar = needParentheses(al, i, f); // tells if are needed parentheses around a formula
        al.remove(i);
        f = removeNeedlessParen(al, i, f);
        /* predQ: array list of the token images to add before the first index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList<String> predQ = new ArrayList<>();
        if(needPar) { predQ.add("("); f += 1; }
        /* nextQ: array list of the token images to add after the last index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList nextQ = new ArrayList<>(Arrays.asList("S", "false"));
        if(needPar) { nextQ.add(")"); }
        al.addAll(i, predQ);
        al.addAll(f, nextQ);
        return f;
    }

    /** rewriting rule: F(q) =>* (q U true) */
    public static int ruleF(ArrayList<String> al, int i, int f){
        boolean needPar = needParentheses(al, i, f); // tells if are needed parentheses around a formula
        al.remove(i);
        f = removeNeedlessParen(al, i , f);
        /* predQ: array list of the token images to add before the first index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList<String> predQ = new ArrayList<>();
        if(needPar) { predQ.add("("); f += 1; }
        /* nextQ: array list of the token images to add after the last index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList<String> nextQ = new ArrayList<>(Arrays.asList("U", "true"));
        if(needPar) { nextQ.add(")"); }
        al.addAll(i, predQ);
        al.addAll(f, nextQ);
        return f;
    }

    /** rewriting rule: G(q) =>* !(!q U true) */
    public static int ruleG(ArrayList<String> al, int i, int f){
        al.remove(i);
        f = removeNeedlessParen(al, i, f);
        /* predQ: array list of the token images to add before the first index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList<String> predQ = new ArrayList<>(Arrays.asList("!", "(", "!")); f += 3;
        /* nextQ: array list of the token images to add after the last index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList<String> nextQ = new ArrayList<>(Arrays.asList("U", "true", ")"));
        al.addAll(i, predQ);
        al.addAll(f, nextQ);
        return f;
    }

    /** rewriting rule: X(q) =>* (q U false) */
    public static int ruleX(ArrayList<String> al, int i, int f){
        boolean needPar = needParentheses(al, i, f); // tells if are needed parentheses around a formula
        al.remove(i);
        f = removeNeedlessParen(al, i, f);
        /* predQ: array list of the token images to add before the first index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList predQ = new ArrayList<String>();
        if(needPar) { predQ.add("("); f += 1; }
        /* nextQ: array list of the token images to add after the last index of q, where
        q is the formula in the scope of the unary operator */
        ArrayList<String> nextQ = new ArrayList<>(Arrays.asList("U", "false"));
        if(needPar) { nextQ.add(")"); }
        al.addAll(i, predQ);
        al.addAll(f, nextQ);
        return f;
    }

    /** Removes redundant parentheses surrounding an atom and returns f.
     * If no parentheses are removed then the returning f will be the same got in input. */
    public static int removeNeedlessParen(ArrayList<String> al, int i, int f){
        int d = f-i; //difference between the end and the beginning indexes of the formula
        if(d == 3 && al.get(i) == "(" && al.get(f-1) == ")"){
            al.remove(--f);
            al.remove(i); f -= 1;
        }
        return f;
    }

    /** Returns the position of the first formula's token. */
    /* pre: 0 < index < al.size() && #(left parentheses) == #(right parentheses) */
    public static int beginQ(ArrayList<String> al, int index){
        String nt = al.get(--index);
        if(Pattern.matches(unaryOperator, nt)) {
            while(Pattern.matches(unaryOperator, nt)){
                nt = al.get(++index);
            }
        }
        if(nt == LPAREN) { index = closingParenthesis(al, index); }
        return index;
    }

    /** Returns the position of the left parenthesis corresponding to the right one
     * at position "index" got in input. */
    /* pre: al.get(index) == ")" && #(left parentheses) == #(right parentheses) */
    public static int openingParenthesis(ArrayList<String> al, int index) {
        String nt = al.get(index);
        int count = 1;
        while(count > 0){
            nt = al.get(--index);
            if(nt == LPAREN) count--;
            if(nt == RPAREN) count++;
        }
        return index;
    }

    /** Returns the position of the last formula's token. */
    /* pre: 0 <= index < al.size() && #(left parentheses) == #(right parentheses) */
    public static int endQ(ArrayList<String> al, int index){
        int f = index;
        String nt = al.get(++f);
        if(Pattern.matches(unaryOperator, nt)) {
            while(Pattern.matches(unaryOperator, nt)){
                nt = al.get(++f);
                System.out.println(nt);
            }
        }
        if(nt == LPAREN) { f = closingParenthesis(al, f); }
        return f;
    }

    /** Returns the position of the right parenthesis corresponding to the left one
     * at position "index" got in input. */
    /* pre: al.get(index) == "(" && #(left parentheses) == #(right parentheses) */
    public static int closingParenthesis(ArrayList<String> al, int index) {
        int f = index;
        String nt = al.get(f);
        int count = 1;
        while(count > 0){
            nt = al.get(++f);
            if(nt == LPAREN) count++;
            if(nt == RPAREN) count--;
        }
        return f;
    }

    /** Tells us if is required to surround the formula with parentheses. */
    /* pre: i <= f */
    public static boolean needParentheses(ArrayList<String> al, int i, int f){
        boolean need = true;
        if (i > 0  && f < al.size()-1) { /* if the two indexes are inside the ArrayList range then */
            String prec = al.get(i-1); // token image preceding the formula
            String follow = al.get(f+1); // token image following the formula
            /* if the formula is preceded by a left parenthesis and followed by a right one
            then no further parentheses are needed */
            if (prec == LPAREN && follow == RPAREN) need = false;
        }
        return need;
    }

}
