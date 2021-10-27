package separator;

import formula.AtomicFormula;
import formula.Formula;
import java.util.ArrayList;
import static formula.AtomConstant.TRUE;

public class PureFormulaeMatrix {

    ArrayList<Formula[]> matrix;

    public PureFormulaeMatrix() { matrix = new ArrayList<>(); }

    public void addTriple(Formula f_past, Formula f_present, Formula f_future) {
        matrix.add( new Formula[] {f_past, f_present, f_future} );
    }

    public void addPureTriple(Formula f) {

        if(!f.isPure()) throw new IllegalArgumentException(
                "f must be a pure formula!"
        );

        switch (f.getTime()) {
            case PAST -> this.addPurePastTriple(f);
            case PRESENT -> this.addPurePresentTriple(f);
            case FUTURE -> this.addPureFutureTriple(f);
        }

    }

    private void addPurePastTriple(Formula f_past) {
        addTriple(
                f_past,
                new AtomicFormula(TRUE),
                new AtomicFormula(TRUE)
        );
    }

    private void addPurePresentTriple(Formula f_present) {
        addTriple(
                new AtomicFormula(TRUE),
                f_present,
                new AtomicFormula(TRUE)
        );
    }

    private void addPureFutureTriple(Formula f_future) {
        addTriple(
                new AtomicFormula(TRUE),
                new AtomicFormula(TRUE),
                f_future
        );
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for ( Formula[] v : matrix) {
            if(v != matrix.get(0)) str.append(",\n");
            str.append(String.format(" [\"%s\", \"%s\", \"%s\"]", v[0], v[1], v[2]));

        }
        return String.format("[\n%s\n]", str);
    }

    public void print() { System.out.println(this); }


}
