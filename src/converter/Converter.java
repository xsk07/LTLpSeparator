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
        for(int i = 0; i < al.size(); i++){
            String ti = al.get(i);
            int f;
            ArrayList predQ = new ArrayList();
            ArrayList nextQ = new ArrayList();
            switch (ti){
                case "H": // H(q) == !(!q S false)
                    f = endQ(al, i);
                    al.remove(i);
                    predQ = new ArrayList<>(Arrays.asList("!", "(", "!"));
                    nextQ = new ArrayList<>(Arrays.asList("S", "false", ")"));
                    al.addAll(i, predQ);
                    al.addAll(f+3, nextQ);
                    break;
                case "Y": // Y(q) == (q S false)
                    f = endQ(al, i);
                    al.remove(i);
                    predQ.add("(");
                    nextQ = new ArrayList<>(Arrays.asList("S", "false", ")"));
                    al.addAll(i, predQ);
                    al.addAll(f+1, nextQ);
                    break;
                case "F": // F(q) == (q U true)
                    f = endQ(al, i);
                    al.remove(i);
                    predQ.add("(");
                    nextQ = new ArrayList<>(Arrays.asList("U", "true", ")"));
                    al.addAll(i, predQ);
                    al.addAll(f+1, nextQ);
                    break;
                case "G": // G(q) == !(!q U true)
                    f = endQ(al, i);
                    al.remove(i);
                    predQ = new ArrayList<>(Arrays.asList("!", "(", "!"));
                    nextQ = new ArrayList<>(Arrays.asList("U", "true", ")"));
                    al.addAll(i, predQ);
                    al.addAll(f+3, nextQ);
                    break;
                case "X": // X(q) == (q U false)
                    f = endQ(al, i);
                    al.remove(i);
                    predQ.add("(");
                    nextQ = new ArrayList<>(Arrays.asList("U", "false", ")"));
                    al.addAll(i, predQ);
                    al.addAll(f+1, nextQ);
                    break;
                default: break;
            }
        }
    }

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
}
