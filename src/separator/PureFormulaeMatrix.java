package separator;

import formula.AtomicFormula;
import formula.Formula;
import java.util.ArrayList;
import static formula.AtomConstant.TRUE;

public class PureFormulaeMatrix {

    ArrayList<Formula[]> matrix;

    public PureFormulaeMatrix() { matrix = new ArrayList<>(); }

    /** Adds a triple to the matrix. */
    public void addTriple(Formula f_past, Formula f_present, Formula f_future) {
        matrix.add(new Formula[] {f_past, f_present, f_future});
    }

    /** Adds to the matrix a triple where all the components are the atomic formula 'true'
     *  except to the one which corresponds to the pure formula got in input.
     *  @param f a pure past, present of future formula */
    public void addPureTriple(Formula f) {
        if(!f.isPure()) throw new IllegalArgumentException(
                "f must be a pure formula"
        );
        switch (f.getTime()) {
            case PAST -> this.addPurePastTriple(f);
            case PRESENT -> this.addPurePresentTriple(f);
            case FUTURE -> this.addPureFutureTriple(f);
        }
    }

    /** Adds to the matrix a triple where all the components are the atomic formula 'true'
     *  except to the past one, which corresponds to the formula got in input.
     *  @param f_past a pure past formula */
    private void addPurePastTriple(Formula f_past) {
        this.addTriple(f_past, new AtomicFormula(TRUE), new AtomicFormula(TRUE));
    }

    /** Adds to the matrix a triple where all the components are the atomic formula 'true'
     *  except to the present one, which corresponds to the formula got in input.
     *  @param f_present a pure present formula */
    private void addPurePresentTriple(Formula f_present) {
        this.addTriple(new AtomicFormula(TRUE), f_present, new AtomicFormula(TRUE));
    }

    /** Adds to the matrix a triple where all the components are the atomic formula 'true'
     *  except to the future one, which corresponds to the formula got in input.
     *  @param f_future a pure future formula */
    private void addPureFutureTriple(Formula f_future) {
        this.addTriple(new AtomicFormula(TRUE), new AtomicFormula(TRUE), f_future);
    }

    /** Translate the */
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
