package separator;

import formula.Formula;
import java.util.concurrent.Callable;

public class SeparationTask implements Callable<Formula> {

    private static final FormulaSeparator separator = new FormulaSeparator();
    private final Formula phi;
    private Formula separatedPhi;

    public SeparationTask(Formula f) { phi = separatedPhi = f; }

    @Override
    public Formula call() throws Exception {
        separatedPhi = separator.separate(phi);
        return separatedPhi;
    }

}
