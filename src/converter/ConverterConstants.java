package converter;

public interface ConverterConstants {

    /** String constants */
    String LPAREN = "(";
    String RPAREN = ")";
    String NOT = "!";
    String AND = "&";
    String OR = "|";
    String IMPL = "->";
    String EQUIV = "<->";
    String TRUE = "true";
    String FALSE = "false";
    String UNTIL = "U";
    String  SINCE = "S";
    String ONCE = "O";
    String HIST = "H";
    String YEST = "Y";
    String FIN = "F";
    String GLOB = "G";
    String NEXT = "X";
    String ATOM = "[a-z] ([a-z,0-9,_])*";

    /** String regex definition of unary operators */
    String unaryOperator = "!|O|H|Y|F|G|X";

}
