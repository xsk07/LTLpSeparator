package formula;
import java.util.ArrayList;
import java.util.Iterator;

import static formula.AtomConstant.*;
import static formula.Operator.AND;
import static formula.Operator.OR;

public class FormulaeList extends ArrayList<Formula> {

    public Formula toDisjunctionFormula() {
        if(this.isEmpty()) return new AtomicFormula(FALSE);
        return toCombinationFormula(OR);
    }

    public Formula toConjunctionFormula() {
        if(this.isEmpty()) return new AtomicFormula(TRUE);
        return toCombinationFormula(AND);
    }

    private Formula toCombinationFormula(Operator op) {
        if(this.isEmpty()) throw new IndexOutOfBoundsException (
                "the list should contain at least one element"
        );
        Iterator<Formula> itr = this.iterator();
        Formula prev = itr.next();
        while (itr.hasNext()) prev = combine(op, prev, itr.next());
        return prev;
    }

    private BinaryFormula combine(Operator op, Formula f1, Formula f2) {
        if(!op.isBinary()) throw new IllegalArgumentException(
                "the operator in input should be a binary operator"
        );
        if(this.isEmpty()) throw new IndexOutOfBoundsException (
                "the list should contain at least one element"
        );
        return new BinaryFormula(op, f1, f2);
    }


}
