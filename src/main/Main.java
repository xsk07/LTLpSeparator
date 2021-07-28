package main;

import parser.ParseException;
import parser.Parser;
import parser.TokenList;
import java.util.ArrayList;
import converter.*;

public class Main {
    public static void main(String[] args) throws ParseException {
        Parser parser = new Parser(System.in);
        TokenList tl = parser.Input();
        ArrayList al = tl.toArrayList();
        ArrayList alc = arrayListDeepCopy(al);
        Converter cnv = new Converter();
        cnv.toBinaryForm(alc);
        System.out.println(al.toString());
        System.out.println(alc.toString());
    }

    public static ArrayList<String> arrayListDeepCopy(ArrayList<String> al){
        ArrayList alc = new ArrayList();
        alc.addAll(al);
        return alc;
    }

}
