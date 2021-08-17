package trashbin;

import parser.Token;
import java.util.ArrayList;

public class TokenList {

    private Token head;
    private Token tail;

    TokenList(){
        this.head = null;
        this.tail = null;
    }

    TokenList(Token head, Token tail ) {
        this.head = head;
        this.tail = tail;
    }

    public Token getHead(){
       return head;
    }

    public Token getTail(){
        return tail;
    }

    /** TokenList deep copy. */
    public TokenList deepCopy() {
        TokenList cl = new TokenList();
        Token prev = null;
        for (Token p = head; p != tail; p = p.next) {
            Token t = p.deepCopy();
            if(p == head) cl.head = t;
            if(p == tail) cl.tail = t;
            if(prev != null) prev.next = t;
            prev = t;
        }
        return  cl;
    }

    int lenght(){
        int len = 0;
        for (Token p = head; p.next != null; p = p.next) {
            len += 1;
        }
        return len;
    }

    public String toString() {
        String s = "";
        for (Token p = head; p != tail; p = p.next) {
            s += p.toString();
        }
        return s;
    }

    public ArrayList<String> toArrayList() {
        ArrayList al = new ArrayList();
        for (Token p = head; p != tail; p = p.next) {
            al.add(p.toString());
        }
        return al;
    }

    public void print () {
        System.out.print("[");
        for (Token p = head; p != tail; p = p.next) {
            System.out.print(p.toString());
            if(p.next != tail) System.out.print(",");
        }
        System.out.print("]");
    }

}


