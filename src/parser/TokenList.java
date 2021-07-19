package parser;

public class TokenList {

    private Token head;
    private Token tail;

    TokenList (Token head, Token tail ) {
        this.head = head;
        this.tail = tail;
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


