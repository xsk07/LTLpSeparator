package converter;
import java.util.ArrayList;
import java.util.Arrays;


/** Formula converter. */
public class Converter {

    public static void Converter(){}

    /**
     * Converts a formula containing unary temporal operators into an equivalent form
     * without them.
     */
    public static void toBinaryForm(ArrayList<String> al) {
        // al: ArrayList of token images
        for(int i = 0; i < al.size(); i++){
            String ti = al.get(i);
            int f, d; boolean needPar;
            ArrayList predQ = new ArrayList();
            ArrayList nextQ = new ArrayList();
            switch (ti){
                case "H": // H(q) == !(!q S false)
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
                case "Y": // Y(q) == (q S false)
                    f = endQ(al, i);
                    d = f-i;
                    needPar = needParenteses(al, i, f);
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
                case "F": // F(q) == (q U true)
                    f = endQ(al, i);
                    d = f-i;
                    needPar = needParenteses(al, i, f);
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
                case "G": // G(q) == !(!q U true)
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
                case "X": // X(q) == (q U false)
                    f = endQ(al, i);
                    d = f-i;
                    needPar = needParenteses(al, i, f);
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

    // pre: 0 <= index < al.size()
    public static int endQ(ArrayList<String> al, int index){
        int f = index;
        String t = al.get(++f);
        if(t == "(") {
            int count = 1;
            while(count > 0){
                String nt = al.get(++f);
                if(nt == "(") count++;
                if(nt == ")") count--;
            }
        }
        return f;
    }

    // pre: i <= f
    public static boolean needParenteses(ArrayList<String> al, int i, int f){
        boolean need = true;
        if (i > 0){
            String pred = al.get(i-1);
            String next = al.get(f+1);
            if (pred == "(" && next == ")") need = false;
        }
        return need;
    }

}
