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
            int f; // the last index of the formula referred by the unary operator
            int d; /* difference between the end and the beginning indexes of the formula inside
            the parentheses */
            boolean needPar; // tells if are needed parentheses around a formula
            ArrayList predQ = new ArrayList(); /* array list of the token images to add before the first index of q */
            ArrayList nextQ = new ArrayList(); /* array list of the token images to add after the last index of q */
            /* where q is the formula on which the unary operator is applied to */
            switch (ti){
                case ONCE: // rewriting rule: O(q) =>* (q S true)
                    f = endQ(al, i);
                    d = f-i;
                    needPar = needParentheses(al, i, f);
                    al.remove(i); // deletes the token image relative to the unary operator
                    // begin: elimination of redundant parentheses surrounding an atom
                    if(d == 3 && al.get(i) == "(" && al.get(f-1) == ")"){
                        al.remove(--f);
                        al.remove(i); f -= 1;
                    } // end
                    if(needPar) { predQ.add("("); f += 1; }
                    nextQ = new ArrayList<>(Arrays.asList("S", "true"));
                    if(needPar) { nextQ.add(")"); }
                    al.addAll(i, predQ);
                    al.addAll(f, nextQ);
                    break;
                case HIST: // rewriting rule: H(q) =>* !(!q S false)
                    f = endQ(al, i);
                    d = f-i;
                    al.remove(i);
                    // begin: elimination of redundant parentheses surrounding an atom
                    if(d == 3 && al.get(i) == "(" && al.get(f-1) == ")"){
                        al.remove(--f);
                        al.remove(i); f -= 1;
                    } // end
                    predQ = new ArrayList<>(Arrays.asList("!", "(", "!")); f += 3;
                    nextQ = new ArrayList<>(Arrays.asList("S", "false", ")"));
                    al.addAll(i, predQ);
                    al.addAll(f, nextQ);
                    break;
                case YEST: // rewriting rule: Y(q) =>* (q S false)
                    f = endQ(al, i);
                    d = f-i;
                    needPar = needParentheses(al, i, f);
                    al.remove(i);
                    // begin: elimination of redundant parentheses surrounding an atom
                    if(d == 3 && al.get(i) == "(" && al.get(f-1) == ")"){
                        al.remove(--f);
                        al.remove(i); f -= 1;
                    } // end
                    if(needPar) { predQ.add("("); f += 1; }
                    nextQ = new ArrayList<>(Arrays.asList("S", "false"));
                    if(needPar) { nextQ.add(")"); }
                    al.addAll(i, predQ);
                    al.addAll(f, nextQ);
                    break;
                case FIN: // rewriting rule: F(q) =>* (q U true)
                    f = endQ(al, i);
                    d = f-i;
                    needPar = needParentheses(al, i, f);
                    al.remove(i);
                    // begin: elimination of redundant parentheses surrounding an atom
                    if(d == 3 && al.get(i) == "(" && al.get(f-1) == ")"){
                        al.remove(--f);
                        al.remove(i); f -= 1;
                    } // end
                    if(needPar) { predQ.add("("); f += 1; }
                    nextQ = new ArrayList<>(Arrays.asList("U", "true"));
                    if(needPar) { nextQ.add(")"); }
                    al.addAll(i, predQ);
                    al.addAll(f, nextQ);
                    break;
                case GLOB: // rewriting rule: G(q) =>* !(!q U true)
                    f = endQ(al, i);
                    d = f-i;
                    al.remove(i);
                    // begin: elimination of redundant parentheses surrounding an atom
                    if(d == 3 && al.get(i) == "(" && al.get(f-1) == ")"){
                        al.remove(--f);
                        al.remove(i); f -= 1;
                    } // end
                    predQ = new ArrayList<>(Arrays.asList("!", "(", "!")); f += 3;
                    nextQ = new ArrayList<>(Arrays.asList("U", "true", ")"));
                    al.addAll(i, predQ);
                    al.addAll(f, nextQ);
                    break;
                case NEXT: // rewriting rule: X(q) =>* (q U false)
                    f = endQ(al, i);
                    d = f-i;
                    needPar = needParentheses(al, i, f);
                    al.remove(i);
                    // begin: elimination of redundant parentheses surrounding an atom
                    if(d == 3 && al.get(i) == "(" && al.get(f-1) == ")"){
                        al.remove(--f);
                        al.remove(i); f -= 1;
                    } // end
                    if(needPar) { predQ.add("("); f += 1; }
                    nextQ = new ArrayList<>(Arrays.asList("U", "false"));
                    if(needPar) { nextQ.add(")"); }
                    al.addAll(i, predQ);
                    al.addAll(f, nextQ);
                    break;
                default: break;
            }
        }
    }

    /** Returns the position of the first formula's token. */
    /* pre: 0 <= index < al.size() && #(left parentheses) == #(right parentheses) */
    public static int beginQ(ArrayList<String> al, int index){
        int i = index;
        String t = al.get(--i);
        if(t == RPAREN) {
            int count = 1;
            while(count > 0) {
                String nt = al.get(--i);
                if(nt == RPAREN) count++;
                if(nt == LPAREN) count--;
            }
        }
        return i;
    }

    /** Returns the position of the last formula's token. */
    /* pre: 0 <= index < al.size() && #(left parentheses) == #(right parentheses) */
    public static int endQ(ArrayList<String> al, int index){
        int f = index;
        String nt = al.get(++f);
        if(Pattern.matches(unaryOperator, nt)) {
            while(Pattern.matches(unaryOperator, nt)){
                nt = al.get(++f);
            }
        }
        if(nt == LPAREN) { f = closingParenthesis(al, f); }
        return f;
    }

    /** Returns the position of the right parenthesis corresponding to the left one
     * at position "index" got in input. */
    /* pre: al.get(i) == "(" && #(left parentheses) == #(right parentheses) */
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
